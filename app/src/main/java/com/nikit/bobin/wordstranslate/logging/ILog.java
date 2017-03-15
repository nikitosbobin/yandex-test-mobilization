package com.nikit.bobin.wordstranslate.logging;

public interface ILog {
    void info(Object object);
    void info(String message);
    void info(String format, String... args);

    void debug(Object object);
    void debug(String message);
    void debug(String format, String... args);

    void warn(Object object);
    void warn(String message);
    void warn(String format, String... args);

    void error(Object object);
    void error(String message);
    void error(String format, String... args);
}
