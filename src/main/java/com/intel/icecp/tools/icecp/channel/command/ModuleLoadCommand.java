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

import com.intel.icecp.core.management.Channels;
import com.intel.icecp.tools.icecp.channel.exceptions.FailedResponseException;

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
