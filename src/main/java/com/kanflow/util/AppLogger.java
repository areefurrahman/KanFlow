package com.kanflow.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logger wrapper for KanFlow.
 * All application events, errors, and warnings go through here.
 */
public class AppLogger {

    private AppLogger() {} // utility class — no instantiation

    public static Logger get(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}