package com.nikit.bobin.wordstranslate.translating;

import android.support.annotation.Nullable;
import android.util.Log;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.HttpMethod;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import java.util.concurrent.Callable;

import okhttp3.Response;

public class YandexTranslator implements ITranslator {
    private final IYandexRestApiUriFactory uriFactory;
    private final IYandexResponseExtractor responseExtractor;
    private final Language ui;
    private IHttpSender httpSender;

    private DeferredManager deferredManager;

    private YandexTranslatorCache cache;
    private ILanguagesDatabase languagesDatabase;

    public YandexTranslator(
            DeferredManager deferredManager,
            IHttpSender httpSender,
            Language ui,
            IYandexRestApiUriFactory uriFactory,
            IYandexResponseExtractor responseExtractor,
            ILanguagesDatabase languagesDatabase,
            @Nullable YandexTranslatorCache cache) {
        Ensure.notNull(deferredManager, "deferredManager");
        Ensure.notNull(httpSender, "httpSender");
        Ensure.notNull(uriFactory, "uriFactory");
        Ensure.notNull(responseExtractor, "responseExtractor");
        Ensure.notNull(ui, "ui");
        Ensure.notNull(languagesDatabase, "languagesDatabase");

        this.languagesDatabase = languagesDatabase;
        this.deferredManager = deferredManager;
        this.httpSender = httpSender;
        this.uriFactory = uriFactory;
        this.responseExtractor = responseExtractor;
        this.ui = ui;
        this.cache = cache;
    }

    @Override
    public Promise<TranslatedText, Throwable, Void> translateAsync(final Translation translation) {
        Ensure.notNull(translation, "translation");

        if (cache != null && cache.hasTranslation(translation))
            return createPromiseFromResult(cache.getTranslation(translation));

        String translateUrl = uriFactory.translate(
                translation.getDirection(),
                translation.getOriginalText());
        Promise<TranslatedText, Throwable, Void> promise = httpSender
                .sendRequestAsync(translateUrl, HttpMethod.GET, null)
                .then(extractTranslation(translation));

        if (cache != null)
            promise = promise.then(cache.addTranslationFilter());

        return promise;
    }

    @Override
    public Promise<Language[], Throwable, Void> getLanguagesAsync() {
        if (cache != null && cache.langsCached(ui))
            return createPromiseFromResult(cache.getLanguages(ui));

        String getLangsUrl = uriFactory.getLangs(ui);

        Promise<Language[], Throwable, Void> promise = httpSender
                .sendRequestAsync(getLangsUrl, HttpMethod.GET, null)
                .then(extractLanguages());

        if (cache != null)
            promise = promise.then(cache.addLanguagesFilter(ui));

        return promise;
    }

    @Override
    public Promise<Language, Throwable, Void> detectLanguageAsync(String text) {
        Ensure.notNullOrEmpty(text, "text");

        String detectLangUri = uriFactory.detectLang(text, new Language("ru"), new Language("en"));

        return httpSender
                .sendRequestAsync(detectLangUri, HttpMethod.GET, null)
                .then(new DoneFilter<Response, Language>() {
                    public Language filterDone(Response result) {
                        Language language = responseExtractor.extractDetectedLanguage(result);
                        return languagesDatabase.getLanguage(language.getKey(), ui);
                    }
                });
    }

    @Override
    public Promise<WordLookup, Throwable, Void> getWordLookupAsync(final Translation translation) {
        Ensure.notNull(translation, "translation");

        String[] segments = translation.getOriginalText().split(" ");
        if (segments.length > 1) {
            return createPromiseFromResult(null);
        }
        String getLookupUri = uriFactory.dictionaryLookup(translation.getDirection(), translation.getOriginalText());

        return httpSender
                .sendRequestAsync(getLookupUri, HttpMethod.GET, null)
                .then(extractLookup(translation));
    }


    //todo: for this create promise factory
    private <TResult> Promise<TResult, Throwable, Void> createPromiseFromResult(final TResult result) {
        return deferredManager.when(new Callable<TResult>() {
            @Override
            public TResult call() throws Exception {
                return result;
            }
        });
    }

    private DoneFilter<Response, WordLookup> extractLookup(final Translation targetTranslation) {
        return new DoneFilter<Response, WordLookup>() {
            public WordLookup filterDone(Response result) {
                WordLookup wordLookup = responseExtractor.extractWordLookup(result, targetTranslation);
                return wordLookup;
            }
        };
    }

    private DoneFilter<Response, TranslatedText> extractTranslation(final Translation targetTranslation) {
        return new DoneFilter<Response, TranslatedText>() {
            @Override
            public TranslatedText filterDone(Response result) {
                return responseExtractor.extractTranslation(result, targetTranslation);
            }
        };
    }

    private DoneFilter<Response, Language[]> extractLanguages() {
        return new DoneFilter<Response, Language[]>() {
            @Override
            public Language[] filterDone(Response result) {
                return responseExtractor.extractLanguages(result);
            }
        };
    }
}
