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

package com.intel.icecp.cli.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper methods for applying standard options (e.g. -debug, -Dprop=value) to CLI arguments
 *
 */
public class StandardOptions {

    public static final String URI = "uri";
    public static final String COMMAND = "cmd";
    private static final String PARAM = "param";
    private static final String DEBUG = "debug";
    private static final String SYSTEM_PROPERTY = "D";
    private static final String RESPONSE_URI = "responseUri";
    private static final String CONFIG_URI = "configUri";
    public static final String MODULE_URI = "moduleUri";
    public static final String ATTRIBUTE_NAME = "attributeName";

    private StandardOptions() {
        // do not allow instances of this class
    }

    /**
     * Apply standard options to a given list of CLI arguments
     *
     * @param args the list of CLI arguments
     * @return the remaining arguments that do not fit in the options
     * @throws ParseException if the command cannot be parsed
     */
    public static CommandLine apply(String[] args) throws ParseException {
        Options options = create();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // setup logging, TODO eventually this could be more fine-grained (e.g. -debug=WARN)
        if (cmd.hasOption(DEBUG)) {
            Logging.setLog4jLoggingLevel(org.apache.logging.log4j.Level.ALL);
            Logging.setJavaLoggingLevel(java.util.logging.Level.ALL);
        } else {
            Logging.setLog4jLoggingLevel(org.apache.logging.log4j.Level.OFF);
            Logging.setJavaLoggingLevel(java.util.logging.Level.OFF);
        }

        // pass -D properties to Java System Properties
        for (Map.Entry<String, String> e : getPassedSystemProperties(cmd).entrySet()) {
            System.getProperties().put(e.getKey(), e.getValue());
        }

        return cmd;
    }

    /**
     * Retrieve the -D arguments
     *
     * @param cmd the command line arguments
     * @return a map of key-value pairs, e.g. -D a=b
     */
    public static Map<String, String> getPassedSystemProperties(CommandLine cmd) {
        HashMap<String, String> properties = new HashMap<>();
        if (cmd.hasOption(SYSTEM_PROPERTY)) {
            String[] optionValues = cmd.getOptionValues(SYSTEM_PROPERTY);
            for (int i = 0; i < optionValues.length; i += 2) {
                properties.put(optionValues[i], optionValues[i + 1]);
            }
        }
        return properties;
    }

    /**
     * Retrieves the -param arguments and parses them as JSON
     *
     * @param cmd the command line arguments
     * @return an array of the parsed objects
     * @throws IOException if the arguments cannot be read
     */
    public static Object[] getPassedParameters(CommandLine cmd) throws IOException {
        ArrayList<Object> params = new ArrayList<>();
        if (cmd.hasOption(PARAM)) {
            String[] values = cmd.getOptionValues(PARAM);
            ParseParameter parser = new ParseParameter();
            for (String value : values) {
                Object o = parser.buildJsonInput(value);
                params.add(o);
            }
        }
        return params.toArray();
    }

    /**
     * @return options allowed by this application
     * @throws IllegalArgumentException
     */
    private static Options create() {
        Option debug = new Option(DEBUG, "print debugging information");

        Option uri = Option.builder(URI).numberOfArgs(1).valueSeparator()
                .desc("uri to subscribe or publish to").build();

        Option command = Option.builder(COMMAND).numberOfArgs(1).valueSeparator()
                .desc("when running 'execute', the command to be executed on the remote server").build();

        Option param = Option.builder(PARAM).numberOfArgs(1).valueSeparator()
                .desc("when running 'execute', this JSON will be sent as the parameter; multiple instances of this are allowed, e.g. -param 1 -param 2.0 -param \"a\"").build();

        Option configUri = Option.builder(CONFIG_URI).numberOfArgs(1).valueSeparator()
                .desc("when running 'load', provide this path to the module's config file").build();

        Option responseUri = Option.builder(RESPONSE_URI).numberOfArgs(1)
                .valueSeparator()
                .desc("when loading a module or executing a command, the response will be published on this URI; if not specified the tool will auto-generate one")
                .build();

        Option moduleUri = Option.builder(MODULE_URI).numberOfArgs(1)
                .valueSeparator().desc("when loading a module provide this path to a module's JAR").build();

        Option attributeName = Option.builder(ATTRIBUTE_NAME).numberOfArgs(1)
                .valueSeparator().desc("when reading or writing an attribute, identify the attribute using this field").build();

        Option property = Option.builder("D").numberOfArgs(2).valueSeparator()
                .desc("pass Java system properties here, e.g. -D a=b -D c=d; these will be applied to the channel configuration, e.g. -D uri=ndn-lab2.jf.intel.com").build();

        Options options = new Options();
        options.addOption(debug);
        options.addOption(command);
        options.addOption(param);
        options.addOption(configUri);
        options.addOption(uri);
        options.addOption(responseUri);
        options.addOption(moduleUri);
        options.addOption(attributeName);
        options.addOption(property);

        return options;
    }

    public static void printHelpString(String usageString) {
        printHelpString(usageString, create());
    }

    private static void printHelpString(String usageString, Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usageString, options);
    }
}
