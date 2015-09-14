/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.icecp.tools.icecp.channel.util;

/**
 * Utility for setting logging levels programmatically
 */
class Logging {

    private Logging() {
        // do not allow instances of this class
    }

    /**
     * @param level the log4j logging level
     */
    static void setLog4jLoggingLevel(org.apache.logging.log4j.Level level) {
        org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration conf = ctx.getConfiguration();
        conf.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME).setLevel(level);
        ctx.updateLoggers(conf);
    }

    /**
     * @param level the Java logging level
     */
    static void setJavaLoggingLevel(java.util.logging.Level level) {
        java.util.logging.Logger log = java.util.logging.LogManager.getLogManager().getLogger("");
        for (java.util.logging.Handler h : log.getHandlers()) {
            h.setLevel(level);
        }
    }
}
