package com.nikit.bobin.wordstranslate.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.core.Ensure;

public class SettingsProvider {
    private final String CACHING = "caching";
    private final String DICTIONARY = "dictionary";
    private final String PREDICTION = "prediction";
    private SharedPreferences preferences;

    public SettingsProvider(SharedPreferences sharedPreferences) {
        Ensure.notNull(sharedPreferences, "sharedPreferences");

        preferences = sharedPreferences;
    }

    public void setEnableCaching(boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CACHING, value);
        editor.commit();
    }

    public boolean isEnableCaching() {
        return preferences.getBoolean(CACHING, true);
    }

    public void setEnableDictionary(boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DICTIONARY, value);
        editor.commit();
    }

    public boolean isEnableDictionary() {
        return preferences.getBoolean(DICTIONARY, true);
    }

    public void setEnableLangPrediction(boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREDICTION, value);
        editor.commit();
    }

    public boolean isEnableLangPrediction() {
        return preferences.getBoolean(PREDICTION, true);
    }
}
