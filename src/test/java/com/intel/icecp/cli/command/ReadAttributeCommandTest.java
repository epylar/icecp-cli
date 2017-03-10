/*
 * Copyright (c) 2017 Intel Corporation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.icecp.cli.command;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.attributes.AttributesNamespace;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import com.intel.icecp.node.utils.ChannelUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 */
public class ReadAttributeCommandTest extends BaseCommandSetup {
    @Test
    public void basicUsage() throws Exception {
        URI uri = URI.create("mock:/read/attribute");
        String attributeValue = "...";

        Channel<BytesMessage> c = channels.openChannel(ChannelUtils.join(uri, AttributesNamespace.READ_SUFFIX, "a"), BytesMessage.class, Persistence.DEFAULT);
        c.publish(new BytesMessage(attributeValue.getBytes()));

        ReadAttributeCommand command = new ReadAttributeCommand.Builder().setChannels(channels).setPrintStream(printStream).setAttributeName("a").setUri(uri).build();
        command.execute();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(printStream, times(1)).print(captor.capture());
        assertEquals(attributeValue, captor.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void ensureNullsAreNotAllowed() {
        ReadAttributeCommand.Builder builder = new ReadAttributeCommand.Builder();
        builder.build();
    }
}