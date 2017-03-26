package com.nikit.bobin.wordstranslate.storage;

import android.content.Context;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.io.Closeable;
// not need tests
// refactored
public interface ILanguagesDatabase extends Closeable {
    Language getLanguage(String languageKey, Language ui);
    Language[] getLanguages(boolean orderDescending);
    boolean replaceLanguages(Language[] languages, Language ui);
    boolean isLanguagesSaved(Language ui);
    void connect(Context context);
}
