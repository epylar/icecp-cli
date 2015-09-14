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
import com.intel.icecp.core.management.Channels;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import com.intel.icecp.core.misc.ChannelIOException;
import com.intel.icecp.core.misc.ChannelLifetimeException;
import com.intel.icecp.node.utils.ChannelUtils;
import com.intel.icecp.tools.icecp.channel.Command;
import com.intel.icecp.tools.icecp.channel.exceptions.FailedCommandException;
import com.intel.icecp.tools.icecp.channel.util.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Read a remote attribute and print the result to STDOUT
 *
 */
public class ReadAttributeCommand implements Command<Void> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Channels channels;
    private final URI uri;
    private final String attributeName;
    private final int timeoutMs;
    private final PrintStream printStream;

    private ReadAttributeCommand(Channels channels, URI uri, String attributeName, int timeoutMs, PrintStream printStream) {
        this.channels = channels;
        this.uri = uri;
        this.attributeName = attributeName;
        this.timeoutMs = timeoutMs;
        this.printStream = printStream;
    }

    @Override
    public Void execute() throws FailedCommandException {
        LOGGER.info("Reading attribute '{}' from URI: {}", attributeName, uri);
        URI attributeUri = ChannelUtils.join(uri, AttributesNamespace.READ_SUFFIX, attributeName);
        try (Channel<BytesMessage> channel = channels.openChannel(attributeUri, BytesMessage.class, new Persistence(timeoutMs))) {
            BytesMessage message = channel.latest().get(timeoutMs, TimeUnit.MILLISECONDS);
            printStream.print(new String(message.getBytes()));
            return null;
        } catch (ChannelLifetimeException | ChannelIOException | InterruptedException | ExecutionException | TimeoutException e) {
            throw new FailedCommandException(e);
        }
    }

    @Override
    public String toString() {
        return "ReadAttributeCommand{" + "channels=" + channels + ", uri=" + uri + ", attributeName='" + attributeName + '\'' +
                ", timeoutMs=" + timeoutMs + ", printStream=" + printStream + '}';
    }

    /**
     * Builder pattern: use this to correctly assemble the data required for a {@link ReadAttributeCommand}. Usage
     * typically looks like: {@code new Builder().set...().set...()...set().build(); }. TODO factor out the common
     * parts of this and other builders.
     */
    public static class Builder {
        @Validation.NotNull
        private Channels channels;
        @Validation.NotNull
        private URI uri;
        @Validation.NotNull
        private int timeoutMs = 10000;
        @Validation.NotNull
        private PrintStream printStream = System.out;
        @Validation.NotNull
        private String attributeName;

        public Builder setChannels(Channels channels) {
            this.channels = channels;
            return this;
        }

        public Builder setUri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder setAttributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public Builder setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        Builder setPrintStream(PrintStream printStream) {
            this.printStream = printStream;
            return this;
        }

        public ReadAttributeCommand build() {
            LOGGER.debug("Building new 'read' command");
            Validation.checkForNull(this);
            return new ReadAttributeCommand(channels, uri, attributeName, timeoutMs, printStream);
        }
    }
}