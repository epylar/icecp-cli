/*
 * ******************************************************************************
 *
 * INTEL CONFIDENTIAL
 *
 * Copyright 2013 - 2016 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and treaty
 * provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or disclosed
 * in any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 *
 * Unless otherwise agreed by Intel in writing, you may not remove or alter this
 * notice or any other notice embedded in Materials by Intel or Intel's
 * suppliers or licensors in any way.
 *
 * ******************************************************************************
 */

package com.intel.icecp.tools.icecp.channel.command;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.messages.BytesMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import static com.intel.icecp.tools.icecp.channel.command.WriteAttributeCommand.compareAttributeReadWrite;
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
        assertTrue(compareAttributeReadWrite("{\"d\": \"Intel\", \"ts\": 12345}".getBytes(), "{\"d\": \"Intel\"}".getBytes()));
        assertTrue(compareAttributeReadWrite("{\"d\": \"Intel\"}".getBytes(), "{\"d\": \"Intel\", \"ts\": 12345}".getBytes()));
        assertTrue(compareAttributeReadWrite("{\"d\": \"Intel\", \"ts\": 54321}".getBytes(), "{\"d\": \"Intel\", \"ts\": 12345}".getBytes()));
        assertTrue(compareAttributeReadWrite("{\"d\": \"Intel\", \"a\": 54321}".getBytes(), "{\"d\": \"Intel\", \"r\": 12345}".getBytes()));
        assertTrue(compareAttributeReadWrite("{\"1\": \"2\"}".getBytes(), "{\"1\": \"2\"}".getBytes()));
        assertTrue(compareAttributeReadWrite("h3@".getBytes(), "h3@".getBytes()));
    }

    @Test(expected = IllegalStateException.class)
    public void ensureNullsAreNotAllowed() {
        WriteAttributeCommand.Builder builder = new WriteAttributeCommand.Builder();
        builder.build();
    }
}