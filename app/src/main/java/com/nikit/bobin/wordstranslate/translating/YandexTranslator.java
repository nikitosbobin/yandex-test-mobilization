package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.HttpMethod;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

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

    public YandexTranslator(
            DeferredManager deferredManager,
            IHttpSender httpSender,
            Language ui,
            IYandexRestApiUriFactory uriFactory,
            IYandexResponseExtractor responseExtractor,
            YandexTranslatorCache cache) {
        Ensure.notNull(deferredManager, "deferredManager");
        Ensure.notNull(httpSender, "httpSender");
        Ensure.notNull(uriFactory, "uriFactory");
        Ensure.notNull(responseExtractor, "responseExtractor");
        Ensure.notNull(ui, "ui");
        Ensure.notNull(cache, "cache");

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

        if (cache.hasTranslation(translation))
            return createPromiseFromResult(cache.getTranslation(translation));

        String translateUrl = uriFactory.translate(translation.getDirection());

        return httpSender
                .sendRequestAsync(translateUrl, HttpMethod.POST, "text=" + translation.getOriginalText())
                //todo: add screening
                .then(extractTranslation(translation))
                .then(cache.addTranslationFilter());
    }

    @Override
    public Promise<Language[], Throwable, Void> getLanguagesAsync() {
        if (cache.langsCached(ui))
            return createPromiseFromResult(cache.getLanguages(ui));

        String getLangsUrl = uriFactory.getLangs(ui);

        return httpSender
                .sendRequestAsync(getLangsUrl, HttpMethod.POST)
                .then(extractLanguages())
                .then(cache.addLanguagesFilter(ui));
    }

    @Override
    public Promise<Language, Throwable, Void> detectLanguageAsync(String text) {
        Ensure.notNullOrEmpty(text, "text");

        String detectLangUri = uriFactory.detectLang(new Language("ru"), new Language("en"));

        return httpSender
                .sendRequestAsync(detectLangUri, HttpMethod.POST, "text=" + text)
                .then(new DoneFilter<Response, Language>() {
                    @Override
                    public Language filterDone(Response result) {
                        return responseExtractor.extractDetectedLanguage(result);
                    }
                });
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
