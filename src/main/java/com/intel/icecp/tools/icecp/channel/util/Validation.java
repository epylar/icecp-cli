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

package com.intel.icecp.tools.icecp.channel.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 */
public class Validation {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Determine if any {@link NotNull}-tagged fields in the container object are null; if they are, this method will
     * throw an exception.
     *
     * @param container the object to check for null values
     * @throws IllegalStateException if the container has null fields marked with {@link NotNull}
     */
    public static void checkForNull(Object container) {
        for (Field f : container.getClass().getDeclaredFields()) {
            LOGGER.debug("Examining field {} for null", f);
            f.setAccessible(true);
            if (hasNotNullAnnotation(f) && isNull(f, container)) {
                throw new IllegalStateException("Field '" + f + "' should not be null.");
            }
        }
    }

    static boolean hasNotNullAnnotation(Field field) {
        return field.getAnnotation(NotNull.class) != null;
    }

    static boolean isNull(Field field, Object container) {
        try {
            return field.get(container) == null;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    /**
     * Tag fields with this annotation so that this class can verify that they are not null.
     */
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    public @interface NotNull {

    }
}
