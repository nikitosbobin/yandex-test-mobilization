package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.history.IStorage;
import com.nikit.bobin.wordstranslate.history.InMemoryTranslationStorage;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    @Provides
    @Singleton
    IStorage<TranslatedText> provideHistoryStorage() {
        return new InMemoryTranslationStorage();
    }
}
