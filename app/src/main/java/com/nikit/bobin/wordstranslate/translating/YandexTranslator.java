package com.nikit.bobin.wordstranslate.translating;

import android.support.annotation.Nullable;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.net.HttpMethod;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.jdeferred.DeferredManager;
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

        final String translateUrl = uriFactory.translate(
                translation.getDirection(),
                translation.getOriginalText());

        return deferredManager.when(new Callable<TranslatedText>() {
            public TranslatedText call() {
                try {
                    Response response = httpSender.sendRequest(translateUrl, HttpMethod.GET, null);

                    TranslatedText translatedText = responseExtractor.extractTranslation(response, translation);
                    response.close();

                    if (cache != null)
                        cache.addTranslation(translatedText);

                    return translatedText;
                } catch (Exception e) {
                    return TranslatedText.fail(translation);
                }
            }
        });
    }

    @Override
    public Promise<Language[], Throwable, Void> getLanguagesAsync() {
        if (cache != null && cache.hasLanguages(ui))
            return createPromiseFromResult(cache.getLanguages(ui));

        final String getLangsUrl = uriFactory.getLangs(ui);

        return deferredManager.when(new Callable<Language[]>() {
            public Language[] call() throws Exception {
                Response response = httpSender.sendRequest(getLangsUrl, HttpMethod.GET, null);
                Language[] languages = responseExtractor.extractLanguages(response);
                response.close();

                if (cache != null)
                    cache.addLanguages(ui, languages);
                return languages;
            }
        });
    }

    @Override
    public Promise<Language, Throwable, Void> detectLanguageAsync(final String text) {
        Ensure.notNullOrEmpty(text, "text");

        if (cache != null && cache.hasDetection(text))
            return createPromiseFromResult(cache.getDetection(text));

        final String detectLangUri = uriFactory.detectLang(text, new Language("ru"), new Language("en"));

        return deferredManager.when(new Callable<Language>() {
            public Language call() {
                try {
                    Response response = httpSender.sendRequest(detectLangUri, HttpMethod.GET, null);

                    Language language = responseExtractor.extractDetectedLanguage(response);
                    response.close();
                    if (language == null)
                        return null;

                    language = languagesDatabase.getLanguage(language.getKey(), ui);
                    if (cache != null)
                        cache.addDetection(text, language);
                    return language;
                } catch(Exception e) {
                    return null;
                }
            }
        });
    }

    @Override
    public Promise<WordLookup, Throwable, Void> getWordLookupAsync(final Translation translation) {
        Ensure.notNull(translation, "translation");

        if (translation.getWordCount() > 1)
            return createPromiseFromResult(WordLookup.empty(translation));

        if (cache != null && cache.hasLookup(translation))
            return createPromiseFromResult(cache.getWordLookup(translation));

        final String getLookupUri = uriFactory.dictionaryLookup(translation.getDirection(), translation.getOriginalText());

        return deferredManager.when(new Callable<WordLookup>() {
            public WordLookup call() {
                try {
                    Response response = httpSender.sendRequest(getLookupUri, HttpMethod.GET, null);

                    WordLookup wordLookup = responseExtractor.extractWordLookup(response, translation);
                    response.close();

                    if (cache != null)
                        cache.addLookup(wordLookup);

                    return wordLookup;
                } catch (Exception e) {
                    return WordLookup.empty(translation);
                }
            }
        });
    }

    private <TResult> Promise<TResult, Throwable, Void> createPromiseFromResult(final TResult result) {
        return deferredManager.when(new Callable<TResult>() {
            public TResult call() throws Exception {
                return result;
            }
        });
    }
}
