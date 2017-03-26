package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;
import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.LanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.TranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
// not need tests
// refactored
@Module
public class StorageModule {
    @Provides
    @Singleton
    ITranslationsDatabase provideTranslationsDatabase() {
        return new TranslationsDatabase();
    }

    @Provides
    @Singleton
    ILanguagesDatabase provideLanguagesDatabase() {
        return new LanguagesDatabase();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    SettingsProvider provideSettingsProvider(SharedPreferences preferences) {
        return new SettingsProvider(preferences);
    }
}
