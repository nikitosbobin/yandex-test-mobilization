package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;
import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.AnimationsFactory;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.LanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.TranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.settings.SettingsProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides
    @Singleton
    AnimationsFactory provideAnimationsFactory() {
        return new AnimationsFactory();
    }
}
