package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.IYandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.IYandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.YandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexTranslator;
import com.nikit.bobin.wordstranslate.translating.YandexTranslatorCache;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DeferredManager;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TranslationModule {
    @Provides
    ITranslator provideTranslator(
            DeferredManager deferredManager,
            IHttpSender httpSender,
            IYandexRestApiUriFactory uriFactory,
            IYandexResponseExtractor responseExtractor,
            YandexTranslatorCache yandexTranslatorCache) {
        return new YandexTranslator(
                deferredManager,
                httpSender,
                new Language(Locale.getDefault().getLanguage()),
                uriFactory,
                responseExtractor,
                yandexTranslatorCache);
    }

    @Provides
    @Singleton
    YandexTranslatorCache provideYandexTranslatorCache(
            ILanguagesDatabase languagesDatabase,
            ITranslationsDatabase translationsDatabase) {
        return new YandexTranslatorCache(translationsDatabase, languagesDatabase);
    }

    @Provides
    @Singleton
    IYandexRestApiUriFactory provideUriFactory() {
        return new YandexRestApiUriFactory(
                "trnsl.1.1.20170315T155530Z.982270abc72ef811.6b65d3680beb5b85a2f7ee473c7033c589c743a2",
                "dict.1.1.20170317T125256Z.5b1c51624e355923.679ec4c6fe21aca55d9e69779966b0b53d8147cf");
    }

    @Provides
    @Singleton
    IYandexResponseExtractor provideResponseExtractor() {
        return new YandexResponseExtractor();
    }
}
