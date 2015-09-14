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