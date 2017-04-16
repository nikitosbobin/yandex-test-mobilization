package com.nikit.bobin.wordstranslate.storage.settings;

import android.content.SharedPreferences;

public class StringSetting extends Setting<String> {
    public StringSetting(SharedPreferences sharedPreferences, String key, String defaultValue) {
        super(sharedPreferences, key, defaultValue);
    }

    @Override
    protected void saveToSharedPreferences(
            SharedPreferences.Editor editor,
            String key,
            String value) {
        editor.putString(key, value);
    }

    @Override
    protected String preferencesValueGetter(
            SharedPreferences preferences,
            String key,
            String defaultValue) {
        return preferences.getString(key, defaultValue);
    }
}
