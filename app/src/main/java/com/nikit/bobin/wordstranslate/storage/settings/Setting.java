package com.nikit.bobin.wordstranslate.storage.settings;

import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.helpers.Ensure;

/*Abstract class provides convenient access to shared preferences.
* Encapsulate working with SharedPreferences class
*/
public abstract class Setting<TSetting> {
    private SharedPreferences sharedPreferences;
    private TSetting value;
    private String key;

    public Setting(SharedPreferences sharedPreferences, String key, TSetting defaultValue) {
        Ensure.notNull(sharedPreferences, "sharedPreferences");
        Ensure.notNullOrEmpty(key, "key");

        this.sharedPreferences = sharedPreferences;
        this.key = key;
        value = preferencesValueGetter(sharedPreferences, key, defaultValue);
        saveValue(value);
    }

    public TSetting getValue() {
        return value;
    }

    public void setValue(TSetting value) {
        if (value == null || value.equals(this.value))
            return;
        saveValue(value);
        this.value = value;
    }

    private void saveValue(TSetting value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        saveToSharedPreferences(editor, key, value);
        editor.apply();
    }

    protected abstract void saveToSharedPreferences(
            SharedPreferences.Editor editor,
            String key,
            TSetting value);

    protected abstract TSetting preferencesValueGetter(
            SharedPreferences preferences,
            String key,
            TSetting defaultValue);
}
