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

import okhttp3.Response;

public class YandexTranslator implements ITranslator {
    private final IYandexRestApiUriFactory uriFactory;
    private final IYandexResponseExtractor responseExtractor;
    private final Language ui;
    private IHttpSender httpSender;

    private YandexTranslatorCache cache;
    private ILanguagesDatabase languagesDatabase;

    public YandexTranslator(
            IHttpSender httpSender,
            Language ui,
            IYandexRestApiUriFactory uriFactory,
            IYandexResponseExtractor responseExtractor,
            ILanguagesDatabase languagesDatabase,
            @Nullable YandexTranslatorCache cache) {
        Ensure.notNull(httpSender, "httpSender");
        Ensure.notNull(uriFactory, "uriFactory");
        Ensure.notNull(responseExtractor, "responseExtractor");
        Ensure.notNull(ui, "ui");
        Ensure.notNull(languagesDatabase, "languagesDatabase");

        this.languagesDatabase = languagesDatabase;
        this.httpSender = httpSender;
        this.uriFactory = uriFactory;
        this.responseExtractor = responseExtractor;
        this.ui = ui;
        this.cache = cache;
    }

    @Override
    public TranslatedText translate(Translation translation) {
        Ensure.notNull(translation, "translation");

        if (cache != null && cache.hasTranslation(translation))
            return cache.getTranslation(translation);

        String translateUrl = uriFactory.translate(
                translation.getDirection(),
                translation.getOriginalText());

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

    @Override
    public Language[] getLanguages() {
        if (cache != null && cache.hasLanguages(ui))
            return cache.getLanguages(ui);

        String getLangsUrl = uriFactory.getLangs(ui);

        try {
            Response response = httpSender.sendRequest(getLangsUrl, HttpMethod.GET, null);
            Language[] languages = responseExtractor.extractLanguages(response);
            response.close();

            if (cache != null)
                cache.addLanguages(ui, languages);
            return languages;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Language detectLanguage(String text) {
        Ensure.notNullOrEmpty(text, "text");

        if (cache != null && cache.hasDetection(text))
            return cache.getDetection(text);

        String detectLangUri = uriFactory.detectLang(text, new Language("ru"), new Language("en"));

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
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public WordLookup getWordLookup(final Translation translation) {
        Ensure.notNull(translation, "translation");

        if (translation.getWordCount() > 1)
            return WordLookup.empty(translation);

        if (cache != null && cache.hasLookup(translation))
            return cache.getWordLookup(translation);

        String getLookupUri = uriFactory.dictionaryLookup(
                translation.getDirection(),
                translation.getOriginalText());

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
}
