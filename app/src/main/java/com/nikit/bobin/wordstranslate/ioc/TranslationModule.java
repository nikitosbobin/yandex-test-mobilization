package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.IYandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.IYandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.YandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexTranslator;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DeferredManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TranslationModule {
    @Provides
    ITranslator provideTranslator(
            DeferredManager deferredManager,
            IHttpSender httpSender,
            ILog log,
            IYandexRestApiUriFactory uriFactory,
            IYandexResponseExtractor responseExtractor) {
        return new YandexTranslator(deferredManager, httpSender, true, log,
                new Language("ru"), uriFactory, responseExtractor);
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
