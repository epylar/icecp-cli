/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.icecp.tools.icecp.channel.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Combines SPI-loaded implementations with a manual registration process
 *
 */
class ImplementationLoader {

    private static final List manuallyRegisteredImplementations = new ArrayList();

    private ImplementationLoader() {
        // do not create instances of this
    }

    /**
     * @param type the type of the provider to load
     * @return a list of all SPI-provided providers (plus those manually added with {@link #register(Object)})
     */
    @SuppressWarnings("unchecked")
    static <T> List<T> load(Class<T> type) {
        java.util.ServiceLoader<T> load = java.util.ServiceLoader.load(type);
        List<T> implementations = new ArrayList<>();

        // add SPI-loaded implementations
        for (T implementation : load) {
            implementations.add(implementation);
        }

        // add manually registered implementations
        for (Object implementation : manuallyRegisteredImplementations) {
            if (type.isInstance(implementation)) {
                implementations.add((T) implementation);
            }
        }

        return implementations;
    }

    /**
     * @param implementation manually register a provider for use in {@link #load(Class)}
     */
    @SuppressWarnings("unchecked")
    public static void register(Object implementation) {
        manuallyRegisteredImplementations.add(implementation);
    }

    /**
     * Remove all manually registered providers
     */
    public static void clear() {
        manuallyRegisteredImplementations.clear();
    }
}
