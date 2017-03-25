package com.nikit.bobin.wordstranslate.logging;

import android.util.Log;
// refactored
public class SimpleLogger implements ILog {
    private String tag;

    public SimpleLogger(String tag) {
        this.tag = tag;
    }

    public SimpleLogger() {
        this("SimpleLogger");
    }

    @Override
    public void info(Object object) {
        info(object.toString());
    }

    @Override
    public void info(String message) {
        Log.i(tag, message);
    }

    @Override
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    @Override
    public void debug(Object object) {
        debug(object.toString());
    }

    @Override
    public void debug(String message) {
        Log.d(tag, message);
    }

    @Override
    public void debug(String format, Object... args) {
        debug(String.format(format, args));
    }

    @Override
    public void warn(Object object) {
        warn(object.toString());
    }

    @Override
    public void warn(String message) {
        Log.w(tag, message);
    }

    @Override
    public void warn(String format, Object... args) {
        warn(String.format(format, args));
    }

    @Override
    public void error(Object object) {
        error(object.toString());
    }

    @Override
    public void error(String message) {
        Log.e(tag, message);
    }

    @Override
    public void error(String format, Object... args) {
        error(String.format(format, args));
    }
}
