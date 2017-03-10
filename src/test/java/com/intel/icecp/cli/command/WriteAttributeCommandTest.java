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
import com.intel.icecp.core.messages.BytesMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertTrue;

/**
 */
public class WriteAttributeCommandTest extends BaseCommandSetup {

    private static final byte[] NEW_ATTRIBUTE_VALUE = "...".getBytes();
    private WriteAttributeCommand instance;

    @Before
    public void before() {
        URI uri = URI.create("mock:/write/attribute");
        InputStream attributeValue = new ByteArrayInputStream(NEW_ATTRIBUTE_VALUE);
        instance = new WriteAttributeCommand.Builder().setChannels(channels).setAttributeValue(attributeValue).setAttributeName("a").setUri(uri).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void changeAttributeAndMonitor() throws Exception {
        Channel<BytesMessage> readChannel = (Channel<BytesMessage>) mock(Channel.class);
        Channel<BytesMessage> writeChannel = (Channel<BytesMessage>) mock(Channel.class);

        instance.changeAttributeAndMonitor(readChannel, writeChannel, NEW_ATTRIBUTE_VALUE);

        verify(readChannel, times(1)).subscribe(any());
        verify(writeChannel, times(1)).publish(any());
    }

    @Test
    public void testCompareAttributeReadWrite() {
        assertTrue(WriteAttributeCommand.compareAttributeReadWrite("{\"d\": \"Intel\", \"ts\": 12345}".getBytes(), "{\"d\": \"Intel\"}".getBytes()));
        assertTrue(WriteAttributeCommand.compareAttributeReadWrite("{\"d\": \"Intel\"}".getBytes(), "{\"d\": \"Intel\", \"ts\": 12345}".getBytes()));
        assertTrue(WriteAttributeCommand.compareAttributeReadWrite("{\"d\": \"Intel\", \"ts\": 54321}".getBytes(), "{\"d\": \"Intel\", \"ts\": 12345}".getBytes()));
        assertTrue(WriteAttributeCommand.compareAttributeReadWrite("{\"d\": \"Intel\", \"a\": 54321}".getBytes(), "{\"d\": \"Intel\", \"r\": 12345}".getBytes()));
        assertTrue(WriteAttributeCommand.compareAttributeReadWrite("{\"1\": \"2\"}".getBytes(), "{\"1\": \"2\"}".getBytes()));
        assertTrue(WriteAttributeCommand.compareAttributeReadWrite("h3@".getBytes(), "h3@".getBytes()));
    }

    @Test(expected = IllegalStateException.class)
    public void ensureNullsAreNotAllowed() {
        WriteAttributeCommand.Builder builder = new WriteAttributeCommand.Builder();
        builder.build();
    }
}