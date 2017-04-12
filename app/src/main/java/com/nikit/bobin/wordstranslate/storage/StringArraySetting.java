package com.nikit.bobin.wordstranslate.storage;

import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.helpers.Strings;

//Strings array extension of Setting class
public class StringArraySetting extends Setting<String[]> {
    public StringArraySetting(
            SharedPreferences sharedPreferences,
            String key,
            String[] defaultValue) {
        super(sharedPreferences, key, defaultValue);
    }

    @Override
    protected void saveToSharedPreferences(
            SharedPreferences.Editor editor,
            String key,
            String[] value) {
        String join = Strings.join(value, " ");
        editor.putString(key, join);
    }

    @Override
    protected String[] preferencesValueGetter(
            SharedPreferences preferences,
            String key,
            String[] defaultValue) {
        String join = Strings.join(defaultValue, " ");
        String savedValue = preferences.getString(key, join);
        if (savedValue.equals(join))
            return defaultValue;
        return savedValue.split(" ");
    }
}
