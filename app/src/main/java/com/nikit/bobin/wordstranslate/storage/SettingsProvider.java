package com.nikit.bobin.wordstranslate.storage;

import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.core.Ensure;

public class SettingsProvider {
    private BooleanSetting enabledCachingSetting;
    private BooleanSetting enabledDictionarySetting;
    private BooleanSetting enabledPredictionSetting;

    public SettingsProvider(SharedPreferences sharedPreferences) {
        Ensure.notNull(sharedPreferences, "sharedPreferences");

        enabledCachingSetting = new BooleanSetting(sharedPreferences, "caching", true);
        enabledDictionarySetting = new BooleanSetting(sharedPreferences, "dictionary", true);
        enabledPredictionSetting = new BooleanSetting(sharedPreferences, "prediction", true);
    }

    public void setEnableCaching(boolean value) {
        enabledCachingSetting.setValue(value);
    }

    public boolean isEnableCaching() {
        return enabledCachingSetting.getValue();
    }

    public void setEnableDictionary(boolean value) {
        enabledDictionarySetting.setValue(value);
    }

    public boolean isEnableDictionary() {
        return enabledDictionarySetting.getValue();
    }

    public void setEnableLangPrediction(boolean value) {
        enabledPredictionSetting.setValue(value);
    }

    public boolean isEnableLangPrediction() {
        return enabledPredictionSetting.getValue();
    }
}
