package com.intel.icecp.cli.util;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 */
public class ValidationTest {

    private ToValidate subject;

    @Before
    public void setUp() throws Exception {
        subject = new ToValidate();
    }

    @Test(expected = IllegalStateException.class)
    public void checkForNull() throws Exception {
        Validation.checkForNull(subject);
    }

    @Test
    public void hasNotNullAnnotation() throws Exception {
        assertTrue(Validation.hasNotNullAnnotation(getField("a")));
        assertFalse(Validation.hasNotNullAnnotation(getField("d")));
    }

    @Test
    public void isNull() throws Exception {
        assertTrue(Validation.isNull(getField("a"), subject));
        assertFalse(Validation.isNull(getField("d"), subject));
        assertFalse(Validation.isNull(getField("e"), subject));
    }

    private Field getField(String name) throws NoSuchFieldException {
        return ToValidate.class.getDeclaredField(name);
    }

    private static class ToValidate {
        @Validation.NotNull
        Object a;

        @Validation.NotNull
        String b;

        @Validation.NotNull
        int c;

        float d;

        private Object e;
    }
}