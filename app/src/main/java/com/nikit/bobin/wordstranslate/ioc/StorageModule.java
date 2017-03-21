package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.history.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.history.InMemoryTranslationsDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    @Provides
    @Singleton
    ITranslationsDatabase provideTranslationsDatabase() {
        return new InMemoryTranslationsDatabase();
    }
}
