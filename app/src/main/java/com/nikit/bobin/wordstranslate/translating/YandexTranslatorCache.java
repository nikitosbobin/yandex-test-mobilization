package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneFilter;

import java.util.HashMap;

public class YandexTranslatorCache {
    private HashMap<Translation, TranslatedText> translations;
    private Language[] languages;

    public YandexTranslatorCache() {
        translations = new HashMap<>();
    }

    public TranslatedText getTranslation(Translation translation) {
        if (hasTranslation(translation))
            return translations.get(translation);
        return null;
    }

    public boolean hasTranslation(Translation translation) {
        return translations.containsKey(translation);
    }

    public void addTranslation(TranslatedText result) {
        if (result.isSuccess() && !hasTranslation(result.getTranslation())) {
            translations.put(result.getTranslation(), result);
        }
    }

    public void addLanguages(Language[] result) {
        if (!langsCached()) {
            this.languages = languages;
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

    public DoneFilter<Language[], Language[]> addLanguagesFilter() {
        return new DoneFilter<Language[], Language[]>() {
            @Override
            public Language[] filterDone(Language[] result) {
                addLanguages(result);
                return result;
            }
        };
    }

    public Language[] getLanguages() {
        return languages;
    }

    public boolean langsCached() {
        return languages != null;
    }
}
