package com.intel.icecp.cli.command;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 */
public class LatestCommandTest extends BaseCommandSetup {
    @Test(expected = ExecutionException.class)
    public void noData() throws Exception {
        LatestCommand.latest(channels, URI.create("mock:/a/b/c"), 10, printStream);
    }

    @Test
    public void someData() throws Exception {
        URI uri = URI.create("mock:/bytes");

        Channel<BytesMessage> c = channels.openChannel(uri, BytesMessage.class, Persistence.DEFAULT);
        c.publish(new BytesMessage("abc".getBytes()));

        LatestCommand.latest(channels, uri, 10, printStream);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(printStream, times(1)).print(captor.capture());
        assertEquals("abc", captor.getValue());
    }
}