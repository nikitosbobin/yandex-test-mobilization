package com.nikit.bobin.wordstranslate.logging;
// not need tests
// refactored
public interface ILog {
    void info(Object object);
    void info(String message);
    void info(String format, Object... args);

    void debug(Object object);
    void debug(String message);
    void debug(String format, Object... args);

    void warn(Object object);
    void warn(String message);
    void warn(String format, Object... args);

    void error(Object object);
    void error(String message);
    void error(String format, Object... args);
}
