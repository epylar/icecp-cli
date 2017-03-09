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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Publish a stream of bytes as a message on the given channel URI
 *
 */
public class PublishCommand {

    private PublishCommand() {
        // prevent from instantiating any object
    }

    public static void publish(Channels channels, URI uri, int persistenceMs, InputStream inputStream) throws ChannelLifetimeException, IOException, ChannelIOException, InterruptedException {
        Channel<BytesMessage> channel = channels.openChannel(uri, BytesMessage.class, new Persistence(persistenceMs));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream), 32 * 1024 * 1024);
        while (br.ready()) {
            String line = br.readLine();
            if (line != null) {
                BytesMessage message = new BytesMessage(line.getBytes());
                channel.publish(message);
            }
        }

        // some channel types require the channel to stay open for retrieval; TODO remove this when possible (see NdnNotificationChannel)
        Thread.sleep(persistenceMs);
    }
}
