package com.nikit.bobin.wordstranslate.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

//Convenient string extensions
public final class Strings {
    public static String empty = "";

    //Join some objects array in one string with separator
    public static String join(Object[] objects, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < objects.length; ++i) {
            stringBuilder.append(objects[i].toString());
            if (i != objects.length - 1) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }

    //Join some objects collection in one string with separator
    public static String join(Collection<?> objects, String separator) {
        return join(objects.toArray(new Object[objects.size()]), separator);
    }

    //Use java URLEncoder to screen special symbols
    public static String screenSpecialSymbols(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return text;
        }
    }
}
