/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.icecp.cli.command;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.management.Channels;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import com.intel.icecp.core.misc.ChannelIOException;
import com.intel.icecp.core.misc.ChannelLifetimeException;
import com.intel.icecp.cli.Command;
import com.intel.icecp.cli.exceptions.FailedCommandException;
import com.intel.icecp.cli.util.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.net.URI;

/**
 * Print messages received on the subscribed channel (one on each line).
 *
 */
public class SubscribeCommand implements Command<Void> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Channels channels;
    private final URI uri;
    private final PrintStream printStream;

    SubscribeCommand(Channels channels, URI uri, PrintStream printStream) {
        this.channels = channels;
        this.uri = uri;
        this.printStream = printStream;
    }

    /**
     * Open the specified channel and block, printing all messages received on the channel until the application is
     * interrupted
     *
     * @return nothing as this command will print to the given print stream
     * @throws FailedCommandException if the command fails to subscribe
     */
    @Override
    public Void execute() throws FailedCommandException {
        try {
            Channel<BytesMessage> channel = channels.openChannel(uri, BytesMessage.class, Persistence.DEFAULT);
            channel.subscribe(message -> printStream.println(new String(message.getBytes())));
            return blockForever();
        } catch (ChannelLifetimeException | ChannelIOException e) {
            throw new FailedCommandException(e);
        }
    }

    Void blockForever() {
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
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
        private PrintStream printStream = System.out;

        public Builder setChannels(Channels channels) {
            this.channels = channels;
            return this;
        }

        public Builder setUri(URI uri) {
            this.uri = uri;
            return this;
        }

        Builder setPrintStream(PrintStream printStream) {
            this.printStream = printStream;
            return this;
        }

        public SubscribeCommand build() {
            LOGGER.debug("Building new 'read' command");
            Validation.checkForNull(this);
            return new SubscribeCommand(channels, uri, printStream);
        }
    }
}
