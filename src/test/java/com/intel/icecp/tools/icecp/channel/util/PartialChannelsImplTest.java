package com.intel.icecp.tools.icecp.channel.util;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import com.intel.icecp.core.mock.MockChannelProvider;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
public class PartialChannelsImplTest {

    private PartialChannelsImpl instance;

    @Before
    public void beforeTests() {
        instance = new PartialChannelsImpl(new InMemoryConfiguration());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void registerAndUnregister() throws Exception {
        instance.register("mock", new MockChannelProvider());
        instance.unregister("mock");
    }

    @Test
    public void basicUsage() throws Exception {
        instance.register("mock", new MockChannelProvider());
        Channel<BytesMessage> c = instance.openChannel(URI.create("mock:/a/b/c"), BytesMessage.class, Persistence.DEFAULT);
        c.publish(new BytesMessage("abc".getBytes()));
        instance.shutdown();

        assertTrue(instance.pool().isShutdown());
        assertTrue(instance.pool().isTerminated());
    }

    @Test
    public void getOpenChannels() throws Exception {
        assertEquals(0, instance.getOpenChannels().length);

        instance.register("mock", new MockChannelProvider());
        URI uri = URI.create("mock:/a/b/c");
        instance.openChannel(uri, BytesMessage.class, Persistence.DEFAULT);

        assertEquals(1, instance.getOpenChannels().length);
        assertEquals(uri, instance.getOpenChannels()[0]);
    }
}