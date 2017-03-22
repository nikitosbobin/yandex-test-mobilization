package com.nikit.bobin.wordstranslate.storage;

import android.content.Context;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.io.Closeable;

public interface ILanguagesDatabase extends Closeable {
    Language[] getLanguages(boolean orderDescending);
    int getLanguagesCount();
    boolean replaceLanguages(Language[] languages, Language ui);
    boolean isLanguagesSaved(Language ui);
    void connect(Context context);
    boolean isConnected();
}
