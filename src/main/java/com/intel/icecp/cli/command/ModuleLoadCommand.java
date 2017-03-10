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

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 */
public class ModuleLoadCommand {
    private ModuleLoadCommand() {
    }

    public static long load(Channels channels, URI nodeUri, String moduleUriStr, String configUriStr, int timeout)
            throws InterruptedException, ExecutionException, TimeoutException, FailedResponseException {
        URI moduleUri = toUriWithProperScheme(moduleUriStr);
        URI configUri = toUriWithProperScheme(configUriStr);
        Object result = ExecuteCommand.execute(channels, nodeUri, "loadAndStartModules", timeout, moduleUri, configUri);

        @SuppressWarnings("unchecked")
        Collection<Long> loadedModuleIds = (Collection<Long>) result;
        Iterator<Long> ids = loadedModuleIds.iterator();
        if (ids.hasNext())
            return ids.next();
        throw new NoSuchElementException("No module IDs returned; the module failed to load!");
    }

    /**
     * Convert a given location string to URI; this contains special processing to properly prepend "file:" to Windows
     * path names (which otherwise URI believes have a scheme of "C:", e.g.)
     *
     * @param location the location to convert
     * @return a properly-schemed URI
     */
    static URI toUriWithProperScheme(String location) {
        if(location == null || location.isEmpty())
            throw new IllegalArgumentException("This method expects a non-null, non-empty location string");
        URI uri = URI.create(location);

        // no scheme means convert to a file
        if (uri.getScheme() == null) {
            return Paths.get(location).toUri();
        }
        // schemes of size one are likely drive letters on Windows; prepend
        // 'file:'
        else if (uri.getScheme().length() == 1) {
            return Paths.get(location).toUri();
        }

        return uri;
    }
}
