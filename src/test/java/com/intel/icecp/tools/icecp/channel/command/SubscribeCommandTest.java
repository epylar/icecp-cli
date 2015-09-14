package com.intel.icecp.tools.icecp.channel.command;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 */
public class SubscribeCommandTest extends BaseCommandSetup {

    @Test
    public void receivingMessages() throws Exception {
        URI uri = URI.create("mock:/subscribe");
        SubscribeCommand spyCommand = spy(new SubscribeCommand(channels, uri, printStream));
        doReturn(null).when(spyCommand).blockForever();
        spyCommand.execute();

        String message = "abc";
        Channel<BytesMessage> c = channels.openChannel(uri, BytesMessage.class, Persistence.DEFAULT);
        c.publish(new BytesMessage(message.getBytes()));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(printStream, times(1)).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void useBuilder() throws Exception {
        new SubscribeCommand.Builder().setChannels(channels).setUri(null).build();
    }
}