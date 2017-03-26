package com.nikit.bobin.wordstranslate.core;

import java.util.Collection;

public final class Strings {
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

    public static String join(Collection<?> objects, String separator) {
        return join(objects.toArray(new Object[objects.size()]), separator);
    }

    public static String empty = "";
}