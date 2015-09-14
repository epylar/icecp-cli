/*
 * ******************************************************************************
 *
 *  INTEL CONFIDENTIAL
 *
 *  Copyright 2013 - 2016 Intel Corporation All Rights Reserved.
 *
 *  The source code contained or described herein and all documents related to the
 *  source code ("Material") are owned by Intel Corporation or its suppliers or
 *  licensors. Title to the Material remains with Intel Corporation or its
 *  suppliers and licensors. The Material contains trade secrets and proprietary
 *  and confidential information of Intel or its suppliers and licensors. The
 *  Material is protected by worldwide copyright and trade secret laws and treaty
 *  provisions. No part of the Material may be used, copied, reproduced, modified,
 *  published, uploaded, posted, transmitted, distributed, or disclosed in any way
 *  without Intel's prior express written permission.
 *
 *  No license under any patent, copyright, trade secret or other intellectual
 *  property right is granted to or conferred upon you by disclosure or delivery of
 *  the Materials, either expressly, by implication, inducement, estoppel or
 *  otherwise. Any license under such intellectual property rights must be express
 *  and approved by Intel in writing.
 *
 *  Unless otherwise agreed by Intel in writing, you may not remove or alter this
 *  notice or any other notice embedded in Materials by Intel or Intel's suppliers
 *  or licensors in any way.
 *
 * *********************************************************************
 */

package com.intel.icecp.tools.icecp.channel;

import com.intel.icecp.core.management.Channels;
import com.intel.icecp.core.misc.Configuration;
import com.intel.icecp.tools.icecp.channel.command.ExecuteCommand;
import com.intel.icecp.tools.icecp.channel.command.LatestCommand;
import com.intel.icecp.tools.icecp.channel.command.ModuleLoadCommand;
import com.intel.icecp.tools.icecp.channel.command.PublishCommand;
import com.intel.icecp.tools.icecp.channel.command.ReadAttributeCommand;
import com.intel.icecp.tools.icecp.channel.command.SubscribeCommand;
import com.intel.icecp.tools.icecp.channel.command.VersionCommand;
import com.intel.icecp.tools.icecp.channel.command.WriteAttributeCommand;
import com.intel.icecp.tools.icecp.channel.exceptions.UnacceptableCommandException;
import com.intel.icecp.tools.icecp.channel.util.InMemoryConfiguration;
import com.intel.icecp.tools.icecp.channel.util.PartialChannelsImpl;
import com.intel.icecp.tools.icecp.channel.util.StandardOptions;
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