package com.intel.icecp.cli.command;

import com.intel.icecp.core.Node;
import com.intel.icecp.core.attributes.OperatingSystemAttribute;
import com.intel.icecp.node.NodeFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 */
public class ReadAttributeCommandIT {
    @Ignore // until MockChannels can deserialize to a different type than the published one
    @Test
    public void basicUsage() throws Exception {
        Node node = NodeFactory.buildMockNode();
        String expected = node.describe().get(OperatingSystemAttribute.class);

        PrintStream printStream = mock(PrintStream.class);
        ReadAttributeCommand command = new ReadAttributeCommand.Builder().setChannels(node.channels())
                .setPrintStream(printStream).setAttributeName("os").setUri(node.getDefaultUri()).build();
        command.execute();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        assertEquals(expected, captor.getValue());
    }
}
