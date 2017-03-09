package com.intel.icecp.cli.command;

import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;

import static org.junit.Assert.assertArrayEquals;

/**
 */
public class PublishCommandTest extends BaseCommandSetup {
    @Test
    public void publishData() throws Exception {
        String message = "abc";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getBytes());
        URI uri = URI.create("mock:/publish/bytes");

        PublishCommand.publish(channels, uri, 10, inputStream);
        assertArrayEquals(message.getBytes(), channels.openChannel(uri, BytesMessage.class, Persistence.DEFAULT).latest().get().getBytes());
    }
}