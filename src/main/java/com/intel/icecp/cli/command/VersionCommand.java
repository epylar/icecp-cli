/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.icecp.cli.command;

import java.io.PrintStream;

/**
 * Print the current version of the tool; note that this command relies on the maven-jar-plugin and its manifest
 * properties {@code addDefaultImplementationEntries} and {@code addDefaultSpecificationEntries}.
 *
 */
public class VersionCommand {
    public static void execute(PrintStream printStream) {
        String version = VersionCommand.class.getPackage().getImplementationVersion();
        printStream.println(version);
    }
}
