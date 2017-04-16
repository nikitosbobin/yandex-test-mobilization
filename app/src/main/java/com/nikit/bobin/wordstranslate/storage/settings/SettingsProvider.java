package com.nikit.bobin.wordstranslate.storage.settings;

import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.helpers.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.Direction;

//Provides boolean settings than user can change in settings fragment
public class SettingsProvider {
    private BooleanSetting enabledCachingSetting;
    private BooleanSetting enabledDictionarySetting;
    private BooleanSetting enabledPredictionSetting;
    private StringArraySetting recentLanguagesInSelectorView;
    private StringSetting lastDirectionSetting;

    public SettingsProvider(SharedPreferences sharedPreferences) {
        Ensure.notNull(sharedPreferences, "sharedPreferences");

        enabledCachingSetting = new BooleanSetting(sharedPreferences, "caching", true);
        enabledDictionarySetting = new BooleanSetting(sharedPreferences, "dictionary", true);
        enabledPredictionSetting = new BooleanSetting(sharedPreferences, "prediction", true);
        recentLanguagesInSelectorView = new StringArraySetting(
                sharedPreferences,
                "recent_translations_languages",
                new String[0]);
        lastDirectionSetting = new StringSetting(sharedPreferences, "last_direction", null);
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

    public String[] getRecentLanguagesInSelectorView() {
        return recentLanguagesInSelectorView.getValue();
    }

    public void saveRecentLanguagesInSelectorView(String[] recentLanguages) {
        recentLanguagesInSelectorView.setValue(recentLanguages);
    }

    public Direction getLastDirection() {
        String value = lastDirectionSetting.getValue();
        if (value == null)
            return null;
        return Direction.parseFullSerialized(value);
    }

    public void saveLastDirection(Direction direction) {
        if (direction != null)
            lastDirectionSetting.setValue(direction.fullSerialize());
    }
}
