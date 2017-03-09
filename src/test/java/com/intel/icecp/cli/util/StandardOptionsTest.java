package com.intel.icecp.cli.util;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 */
public class StandardOptionsTest {
    @Test
    public void applyJavaSystemProperties() throws Exception {
        CommandLine cmd = StandardOptions.apply("-D uri=x -D a=b".split(" "));
        assertEquals("x", cmd.getOptionValues("D")[1]);
        assertEquals("b", cmd.getOptionValues("D")[3]);
        // TODO eventually handle '-Duri=x'
    }
}