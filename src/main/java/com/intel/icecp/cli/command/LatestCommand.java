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

import java.io.PrintStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Retrieve the latest bytes from the given channel.
 *
 */
public class LatestCommand {
    public static void latest(Channels channels, URI uri, int timeoutMs, PrintStream printStream) throws ChannelIOException, ChannelLifetimeException, InterruptedException, TimeoutException, ExecutionException {
        Channel<BytesMessage> channel = channels.openChannel(uri, BytesMessage.class, new Persistence(timeoutMs));
        BytesMessage message = channel.latest().get(timeoutMs, TimeUnit.MILLISECONDS);
        printStream.print(new String(message.getBytes()));
    }
}
