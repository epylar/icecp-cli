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

import com.intel.icecp.cli.exceptions.FailedResponseException;
import com.intel.icecp.core.management.Channels;
import com.intel.icecp.rpc.CommandRequest;
import com.intel.icecp.rpc.CommandResponse;
import com.intel.icecp.rpc.Rpc;
import com.intel.icecp.rpc.RpcClient;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class calls RpcClient to execute commands.
 *
 */
public class ExecuteCommand {
    private ExecuteCommand() {
    }

    public static Object execute(Channels channels, URI serverUri, String cmd, int timeout, Object... params)
            throws InterruptedException, ExecutionException, NoSuchElementException, FailedResponseException, TimeoutException {

        // create RPC client and call
        RpcClient client = Rpc.newClient(channels, serverUri);
        CommandRequest request = CommandRequest.from(cmd, params);
        CommandResponse response = client.call(request).get(timeout, TimeUnit.SECONDS);

        // check response
        if (response.err) {
            throw new FailedResponseException((String) response.out);
        }

        return response.out;
    }
}
