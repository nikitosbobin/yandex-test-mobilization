package com.nikit.bobin.wordstranslate.storage;

import com.nikit.bobin.wordstranslate.translating.models.Language;

//Abstraction that provides some methods to store languages information
public interface ILanguagesDatabase {
    Language getLanguage(String languageKey, Language ui);

    Language[] getLanguages(boolean orderDescending);

    boolean replaceLanguages(Language[] languages, Language ui);

    boolean isLanguagesSaved(Language ui);
}
