package com.intel.icecp.tools.icecp.channel.command;

import com.intel.icecp.core.mock.MockChannels;
import org.junit.Before;

import java.io.PrintStream;

import static org.mockito.Mockito.mock;

/**
 * Helper class for setting up mock objects for command tests
 *
 */
public class BaseCommandSetup {
    MockChannels channels;
    PrintStream printStream;

    @Before
    public void beforeTests() {
        printStream = mock(PrintStream.class);
        channels = new MockChannels();
    }
}
