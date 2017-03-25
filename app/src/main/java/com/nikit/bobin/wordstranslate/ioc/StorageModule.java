package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;

import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.LanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.NewLanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.NewTranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;
import com.nikit.bobin.wordstranslate.storage.TranslationsDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
    @Provides
    @Singleton
    ITranslationsDatabase provideTranslationsDatabase() {
        return new NewTranslationsDatabase();
    }

    @Provides
    @Singleton
    ILanguagesDatabase provideLanguagesDatabase() {
        return new NewLanguagesDatabase();
    }

    @Provides
    @Singleton
    SettingsProvider provideSettingsProvider(Context context) {
        return new SettingsProvider(context);
    }
}
