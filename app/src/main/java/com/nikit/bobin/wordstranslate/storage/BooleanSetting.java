package com.nikit.bobin.wordstranslate.storage;

import android.content.SharedPreferences;

//Boolean extension of Setting class
public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(SharedPreferences sharedPreferences, String key, Boolean initialValue) {
        super(sharedPreferences, key, initialValue);
    }

    @Override
    protected void saveToSharedPreferences(
            SharedPreferences.Editor editor,
            String key,
            Boolean value) {
        editor.putBoolean(key, value);
    }

    @Override
    protected Boolean preferencesValueGetter(
            SharedPreferences preferences,
            String key,
            Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
}
