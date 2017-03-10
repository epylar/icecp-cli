/*
 * Copyright (c) 2017 Intel Corporation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.icecp.cli;

import com.intel.icecp.cli.command.ExecuteCommand;
import com.intel.icecp.cli.command.LatestCommand;
import com.intel.icecp.cli.command.PublishCommand;
import com.intel.icecp.cli.command.ReadAttributeCommand;
import com.intel.icecp.cli.command.SubscribeCommand;
import com.intel.icecp.cli.command.VersionCommand;
import com.intel.icecp.cli.command.WriteAttributeCommand;
import com.intel.icecp.cli.exceptions.UnacceptableCommandException;
import com.intel.icecp.cli.util.InMemoryConfiguration;
import com.intel.icecp.cli.util.PartialChannelsImpl;
import com.intel.icecp.core.management.Channels;
import com.intel.icecp.core.misc.Configuration;
import com.intel.icecp.cli.command.ModuleLoadCommand;
import com.intel.icecp.cli.util.StandardOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

/**
 */
class Main {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String USAGE_MESSAGE = "icecp-cli publish|subscribe|latest|execute|load|read|help|version [options]";
    private static final String TIMEOUT_PROPERTY = "timeoutInSec";
    private static final int DEFAULT_COMMAND_TIMEOUT_SEC = 10;

    private Main() {
        // prevent from instantiating object outside
    }

    public static void main(String[] args) throws ParseException {
        // the application expects at least one argument
        if (args.length < 1) {
            StandardOptions.printHelpString(USAGE_MESSAGE);
            System.exit(0);
        }

        CommandLine cmd = StandardOptions.apply(args);
        LOGGER.info("Passed arguments: {}", cmd);

        // build proper configuration from java properties
        Configuration configuration = new InMemoryConfiguration();
        configuration.put("uri", "localhost"); // TODO this is a default value for NDN forwading; remove when possible
        for (Entry<String, String> entry : StandardOptions.getPassedSystemProperties(cmd).entrySet()) {
            configuration.put(entry.getKey(), entry.getValue());
        }
        LOGGER.info("Using configuration: {}", configuration);

        // setup channels
        Channels channels = new PartialChannelsImpl(configuration);

        // run command
        String argCmd = args[0];
        LOGGER.info("Running command: {}", argCmd);
        try {
            switch (argCmd) {
                case "subscribe":
                    SubscribeCommand subscribe = new SubscribeCommand.Builder().setChannels(channels).setUri(getUri(cmd)).build();
                    subscribe.execute(); // blocks forever, will not return
                    break;
                case "latest":
                    LatestCommand.latest(channels, getUri(cmd), 2000, System.out);
                    break;
                case "publish":
                    PublishCommand.publish(channels, getUri(cmd), 2000, System.in);
                    break;
                case "execute":
                    Object[] params = StandardOptions.getPassedParameters(cmd);
                    Object response = ExecuteCommand.execute(channels, getUri(cmd), cmd.getOptionValue(StandardOptions.COMMAND),
                            getTimeout(cmd), params);
                    System.out.println(response);
                    break;
                case "load":
                    long moduleIdAfterLoad = ModuleLoadCommand.load(channels, getUri(cmd), cmd.getOptionValue(StandardOptions.MODULE_URI),
                            cmd.getOptionValue("configUri"), getTimeout(cmd));
                    System.out.println(moduleIdAfterLoad);
                    break;
                case "read":
                    ReadAttributeCommand read = new ReadAttributeCommand.Builder().setChannels(channels)
                            .setUri(getUri(cmd)).setAttributeName(cmd.getOptionValue(StandardOptions.ATTRIBUTE_NAME)).build();
                    read.execute();
                    break;
                case "write":
                    WriteAttributeCommand write = new WriteAttributeCommand.Builder().setChannels(channels)
                            .setUri(getUri(cmd)).setAttributeName(cmd.getOptionValue(StandardOptions.ATTRIBUTE_NAME))
                            .setAttributeValue(System.in).build();
                    write.execute();
                    break;
                case "help":
                    StandardOptions.printHelpString(USAGE_MESSAGE);
                    System.exit(0);
                    break;
                case "version":
                    VersionCommand.execute(System.out);
                    System.exit(0);
                    break;
                default:
                    System.err.println("Unrecognized Command Argument : " + argCmd);
                    StandardOptions.printHelpString(USAGE_MESSAGE);
                    System.exit(1);
            }
        } catch (Exception e) { // TODO perhaps catch this differently?
            LOGGER.error("Failed to run command: ", e);
            e.printStackTrace(System.err); // always print stack trace to STDERR to alert the user of the failure
            System.exit(1);
        }

        channels.shutdown();

        System.exit(0); // this is necessary due to some non-daemon threads that are created and never properly closed in icecp-node
    }

    private static URI getUri(CommandLine cmd) throws URISyntaxException, UnacceptableCommandException {
        if (!cmd.hasOption(StandardOptions.URI)) {
            throw new UnacceptableCommandException("URI argument missing; please provide a URI.");
        }
        return new URI(cmd.getOptionValue(StandardOptions.URI));
    }

    private static int getTimeout(CommandLine cmd) throws UnacceptableCommandException {
        Map<String, String> systemProperties = StandardOptions.getPassedSystemProperties(cmd);
        if (!systemProperties.containsKey(TIMEOUT_PROPERTY)) {
            LOGGER.info("Firing command with default timeout value: {}", DEFAULT_COMMAND_TIMEOUT_SEC);
            return DEFAULT_COMMAND_TIMEOUT_SEC;
        }

        String timeoutValue = systemProperties.get(TIMEOUT_PROPERTY);
        int timeout;
        try {
            timeout = Integer.parseInt(timeoutValue);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid value: {} of timeout argument, firing command with default value: {}", timeoutValue, DEFAULT_COMMAND_TIMEOUT_SEC);
            return DEFAULT_COMMAND_TIMEOUT_SEC;
        }
        return timeout <= 0 ? DEFAULT_COMMAND_TIMEOUT_SEC : timeout;
    }
}