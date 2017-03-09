package com.intel.icecp.cli.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 */
public class ModuleLoadCommandTest {
    private static final Logger LOGGER = LogManager.getLogger();

    @Test
    public void missingSchemeOnLinux() throws Exception {
        if (onWindows()) {
            LOGGER.info("Skipping this test as file paths will be modified on Windows, currently on: {}", System.getProperty("os.name"));
            return;
        }
        assertEquals("file:///home/icecp/jar", ModuleLoadCommand.toUriWithProperScheme("/home/icecp/jar").toString());
    }

    @Test
    public void driveLettersOnWindows() throws Exception {
        if (!onWindows()) {
            LOGGER.info("Skipping this test as this expects a Windows C:/ drive, currently on: {}", System.getProperty("os.name"));
            return;
        }
        assertEquals("file:///C:/a/b/c", ModuleLoadCommand.toUriWithProperScheme("C:/a/b/c").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPath() throws Exception {
        ModuleLoadCommand.toUriWithProperScheme(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyString() throws Exception {
        ModuleLoadCommand.toUriWithProperScheme("");
    }

    private boolean onWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}