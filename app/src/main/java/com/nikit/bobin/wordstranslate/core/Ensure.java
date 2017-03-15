package com.nikit.bobin.wordstranslate.core;

import java.net.MalformedURLException;
import java.net.URL;

public class Ensure {
    public static void notNull(Object o, String name) {
        if (o == null)
            throw new NullPointerException(String.format("[%s] should not be null", name));
    }

    public static void notNullOrEmpty(String s, String name) {
        if (s == null || s.equals(""))
            throw new IllegalArgumentException(String.format("[%s] should not be empty", name));
    }

    public static void urlIsPresent(String s, String name) {
        try {
            new URL(s);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("URL: [%s] is not correct", name));
        }
    }
}
