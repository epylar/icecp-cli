/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.icecp.cli.util;

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.messages.ConfigurationMessage;
import com.intel.icecp.core.misc.ChannelIOException;
import com.intel.icecp.core.misc.Configuration;
import com.intel.icecp.core.misc.PropertyNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public class InMemoryConfiguration implements Configuration {

    private final Map<String, Object> map = new HashMap<>();

    @Override
    public void load() throws ChannelIOException {

    }

    @Override
    public void save() throws ChannelIOException {

    }

    @Override
    public <T> T get(String propertyPath) throws PropertyNotFoundException {
        T result = getOrNull(propertyPath);
        if (result == null) {
            throw new PropertyNotFoundException("Could not find: " + propertyPath);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrNull(String propertyPath) {
        return (T) map.get(propertyPath);
    }

    @Override
    public <T> T getOrDefault(T defaultValue, String... propertyPaths) {
        for (String propertyPath : propertyPaths) {
            T value = getOrNull(propertyPath);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    @Override
    public void put(String propertyPath, Object value) {
        map.put(propertyPath, value);
    }

    @Override
    public Channel<ConfigurationMessage> getChannel() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
