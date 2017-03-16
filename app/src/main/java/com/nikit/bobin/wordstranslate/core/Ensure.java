package com.nikit.bobin.wordstranslate.core;

import com.nikit.bobin.wordstranslate.translating.exceptions.NotSuccessfulResponseException;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Response;

public class Ensure {
    public static void notNull(Object o, String name) {
        if (o == null)
            throw new NullPointerException(String.format("[%s] should not be null", name));
    }

    public static void notNullOrEmpty(String s, String name) {
        Ensure.notNull(s, name);

        if (s.equals(""))
            throw new IllegalArgumentException(String.format("[%s] should not be empty", name));
    }

    public static void urlIsPresent(String s, String name) {
        try {
            new URL(s);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("URL: [%s] is not correct", name));
        }
    }

    public static void okHttpResponseIsSuccess(Response response, String name)
            throws NotSuccessfulResponseException {
        if (!response.isSuccessful())
            throw new NotSuccessfulResponseException(String.format("OkHttp response: %s is not success", name));
    }
}
