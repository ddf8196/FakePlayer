package com.ddf.fakeplayer.util;

import java.time.format.DateTimeFormatter;

public abstract class Logger {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("'['yyyy.MM.dd HH:mm:ss'] '");
    private static Logger LOGGER;

    public static void init(Logger logger) {
        LOGGER = logger;
    }

    public static synchronized Logger getLogger() {
        return LOGGER;
    }

    public abstract void log(Object... log);
}
