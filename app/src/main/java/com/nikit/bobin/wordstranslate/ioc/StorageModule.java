package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.LanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.TranslationsDatabase;

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
}
