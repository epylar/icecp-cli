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

package com.intel.icecp.cli.util;

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
