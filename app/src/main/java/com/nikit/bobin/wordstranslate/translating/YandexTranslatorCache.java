package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneFilter;

import java.util.HashMap;

public class YandexTranslatorCache {
    private HashMap<Translation, TranslatedText> translations;
    private Language[] languages;
    private Language languagesUi;
    private ILanguagesDatabase languagesDatabase;
    private ITranslationsDatabase translationsDatabase;

    public YandexTranslatorCache(
            ITranslationsDatabase translationsDatabase,
            ILanguagesDatabase languagesDatabase) {
        Ensure.notNull(translationsDatabase, "translationsDatabase");
        Ensure.notNull(languagesDatabase, "languagesDatabase");

        this.languagesDatabase = languagesDatabase;
        this.translationsDatabase = translationsDatabase;
    }

    private HashMap<Translation, TranslatedText> getTranslations() {
        if (translations == null) {
            translations = new HashMap<>();
            if (translationsDatabase.isConnected()) {
                TranslatedText[] allTranslations = translationsDatabase.getAllTranslations(false);
                for (TranslatedText t : allTranslations)
                    translations.put(t.getTranslation(), t);
            }
        }
        return translations;
    }

    public TranslatedText getTranslation(Translation translation) {
        if (hasTranslation(translation))
            return getTranslations().get(translation);
        return null;
    }

    public boolean hasTranslation(Translation translation) {
        return getTranslations().containsKey(translation);
    }

    public void addTranslation(TranslatedText result) {
        if (result.isSuccess() && !hasTranslation(result.getTranslation())) {
            getTranslations().put(result.getTranslation(), result);
        }
    }

    public void addLanguages(Language[] result, Language ui) {
        if (!langsCached(ui)) {
            this.languages = result;
            languagesUi = ui;
            languagesDatabase.replaceLanguages(result, ui);
        }
    }

    public DoneFilter<TranslatedText, TranslatedText> addTranslationFilter() {
        return new DoneFilter<TranslatedText, TranslatedText>() {
            @Override
            public TranslatedText filterDone(TranslatedText result) {
                addTranslation(result);
                return result;
            }
        };
    }

    public DoneFilter<Language[], Language[]> addLanguagesFilter(final Language ui) {
        return new DoneFilter<Language[], Language[]>() {
            @Override
            public Language[] filterDone(Language[] result) {
                addLanguages(result, ui);
                return result;
            }
        };
    }

    public Language getLanguage(String key, Language ui) {
        Language[] languages = getLanguages(ui);
        for (Language l : languages)
            if (l.getKey().equals(key))
                return l;
        return new Language(key);
    }

    public Language[] getLanguages(Language ui) {
        if (langsCached(ui)) {
            if (languages == null) {
                languages = languagesDatabase.getLanguages(false);
            }
            return languages;
        }
        return new Language[0];
    }

    public boolean langsCached(Language ui) {
        if (languagesUi != null && languagesUi.equals(ui))
            return true;
        return languagesDatabase.isLanguagesSaved(ui);
    }
}
