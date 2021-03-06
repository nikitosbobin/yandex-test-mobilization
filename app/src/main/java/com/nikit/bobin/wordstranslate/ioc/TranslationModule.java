package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;

import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.settings.SettingsProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.IYandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.IYandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.YandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexTranslator;
import com.nikit.bobin.wordstranslate.translating.YandexTranslatorCache;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TranslationModule {
    private final Context context;

    public TranslationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }

    @Provides
    ITranslator provideTranslator(
            IHttpSender httpSender,
            Language ui,
            IYandexRestApiUriFactory uriFactory,
            IYandexResponseExtractor responseExtractor,
            YandexTranslatorCache yandexTranslatorCache,
            ILanguagesDatabase languagesDatabase,
            SettingsProvider settingsProvider) {
        boolean enableCaching = settingsProvider.isEnableCaching();
        if (!enableCaching)
            yandexTranslatorCache = null;
        return new YandexTranslator(
                httpSender,
                ui,
                uriFactory,
                responseExtractor,
                languagesDatabase,
                yandexTranslatorCache);
    }

    @Provides
    @Singleton
    YandexTranslatorCache provideYandexTranslatorCache(
            ILanguagesDatabase languagesDatabase,
            ITranslationsDatabase translationsDatabase) {
        return new YandexTranslatorCache(translationsDatabase, languagesDatabase, 10000);
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

    @Provides
    Language provideUiLanguage() {
        return new Language(Locale.getDefault().getLanguage());
    }
}
