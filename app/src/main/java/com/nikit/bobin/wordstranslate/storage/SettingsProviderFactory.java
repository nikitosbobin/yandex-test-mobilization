package com.nikit.bobin.wordstranslate.storage;

import android.content.Context;

public class SettingsProviderFactory {
    public SettingsProvider createSettingsProvider(Context context) {
        return new SettingsProvider(context);
    }
}
