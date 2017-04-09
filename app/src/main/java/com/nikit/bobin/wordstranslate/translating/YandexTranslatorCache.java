package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import java.util.HashMap;

public class YandexTranslatorCache {
    private HashMap<Translation, TranslatedText> translationsCache;
    private HashMap<Translation, WordLookup> lookupsCache;
    private HashMap<String, Language> detectionsCache;
    private HashMap<Language, Language[]> languagesCache;

    private ILanguagesDatabase languagesDatabase;
    private ITranslationsDatabase translationsDatabase;

    private int cacheMaxSize;

    public YandexTranslatorCache(
            ITranslationsDatabase translationsDatabase,
            ILanguagesDatabase languagesDatabase,
            int cacheMaxSize) {
        Ensure.notNull(translationsDatabase, "translationsDatabase");
        Ensure.notNull(languagesDatabase, "languagesDatabase");
        Ensure.greaterThan(cacheMaxSize, 0, "cacheMaxSize");

        this.languagesDatabase = languagesDatabase;
        this.translationsDatabase = translationsDatabase;
        this.cacheMaxSize = cacheMaxSize;
    }

    private HashMap<Translation, TranslatedText> getTranslationsCache() {
        if (translationsCache == null) {
            translationsCache = new HashMap<>();
            TranslatedText[] allTranslations = translationsDatabase.getAllTranslations(false);
            for (TranslatedText t : allTranslations)
                translationsCache.put(t.getTranslation(), t);
        }
        if (translationsCache.size() >= cacheMaxSize)
            translationsCache = new HashMap<>();
        return translationsCache;
    }

    private HashMap<Translation, WordLookup> getLookupsCache() {
        if (lookupsCache == null || lookupsCache.size() >= cacheMaxSize)
            lookupsCache = new HashMap<>();
        return lookupsCache;
    }

    private HashMap<String, Language> getDetectionsCache() {
        if (detectionsCache == null || detectionsCache.size() >= cacheMaxSize)
            detectionsCache = new HashMap<>();
        return detectionsCache;
    }

    private HashMap<Language, Language[]> getLanguagesCache() {
        if (languagesCache == null || languagesCache.size() >= cacheMaxSize)
            languagesCache = new HashMap<>();
        return languagesCache;
    }

    public TranslatedText getTranslation(Translation translation) {
        if (translation != null && hasTranslation(translation))
            return getTranslationsCache().get(translation);
        return null;
    }

    public boolean hasTranslation(Translation translation) {
        return translation != null && getTranslationsCache().containsKey(translation);
    }

    public void addTranslation(TranslatedText result) {
        if (result != null && result.isSuccess() && !hasTranslation(result.getTranslation())) {
            getTranslationsCache().put(result.getTranslation(), result);
        }
    }

    public boolean hasLookup(Translation translation) {
        return translation != null && getLookupsCache().containsKey(translation);
    }

    public WordLookup getWordLookup(Translation translation) {
        if (translation == null || !hasLookup(translation))
            return null;
        return getLookupsCache().get(translation);
    }

    public boolean addLookup(WordLookup wordLookup) {
        if (wordLookup == null || hasLookup(wordLookup.getTranslation()))
            return false;
        getLookupsCache().put(wordLookup.getTranslation(), wordLookup);
        return true;
    }

    public boolean hasDetection(String text) {
        return text != null && getDetectionsCache().containsKey(text);
    }

    public Language getDetection(String text) {
        if (text == null || !hasDetection(text))
            return null;
        return getDetectionsCache().get(text);
    }

    public boolean addDetection(String text, Language detectedLanguage) {
        if (text == null || detectedLanguage == null || hasDetection(text))
            return false;
        getDetectionsCache().put(text, detectedLanguage);
        return true;
    }

    public boolean hasLanguages(Language ui) {
        if (ui == null)
            return false;
        return getLanguagesCache().containsKey(ui) || languagesDatabase.isLanguagesSaved(ui);
    }

    public Language[] getLanguages(Language ui) {
        if (ui == null || !hasLanguages(ui))
            return null;
        if (getLanguagesCache().containsKey(ui))
            return getLanguagesCache().get(ui);
        Language[] languages = languagesDatabase.getLanguages(true);
        getLanguagesCache().put(ui, languages);
        return languages;
    }

    public boolean addLanguages(Language ui, Language[] languages) {
        if (ui == null || languages == null || hasLanguages(ui))
            return false;
        getLanguagesCache().put(ui, languages);
        return true;
    }
}
