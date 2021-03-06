package com.nikit.bobin.wordstranslate.helpers;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import okhttp3.Response;

//Provide static methods that check correctness of the input arguments
public class Ensure {
    public static void notNull(Object o, String name) {
        if (o == null)
            throw new NullPointerException(String.format("[%s] should not be null", name));
    }

    public static void notNullOrEmpty(String s, String name) {
        Ensure.notNull(s, name);

        if (s.equals(Strings.empty))
            throw new IllegalArgumentException(String.format("[%s] should not be empty", name));
    }

    public static void urlIsPresent(String s, String name) {
        try {
            new URL(s);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("URL: [%s] is not correct", name));
        }
    }

    public static void okHttpResponseIsSuccess(Response response, String name) {
        if (!response.isSuccessful())
            throw new RuntimeException(String.format("OkHttp response: %s is not success", name));
    }

    public static void languageHasTitle(Language language, String name) {
        String title = language.getTitle();
        if (title == null && title.equals(""))
            throw new IllegalArgumentException(String.format("Language implementation: [%s] " +
                    "should has non empty title", name));
    }

    public static <T> void notEmpty(T[] array, String name) {
        if (array.length == 0)
            throw new IllegalArgumentException(String.format("Array: [%s] should not be empty", name));
    }

    public static void inUiThread() {
        String name = Thread.currentThread().getName();
        if (!name.equals("main"))
            throw new IllegalThreadStateException("This method should called from main thread");
    }

    public static void greaterThan(int argument, int targetValue, String name) {
        if (argument <= targetValue)
            throw new IllegalArgumentException(String.format(
                    Locale.getDefault(),
                    "Argument: [%s] should be greater than: %d",
                    name,
                    targetValue));
    }
}
