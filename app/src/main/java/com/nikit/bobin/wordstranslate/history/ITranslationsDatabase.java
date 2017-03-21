package com.nikit.bobin.wordstranslate.history;

import android.content.Context;

import com.nikit.bobin.wordstranslate.functional.OnItemsUpdateListener;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.io.Closeable;

public interface ITranslationsDatabase extends Closeable {
    TranslatedText getById(int id);
    TranslatedText[] getAllTranslations(boolean orderDescending);
    int getAllTranslationsCount();
    TranslatedText[] getFavoriteTranslations(boolean orderDescending);
    int getFavoriteTranslationsCount();
    boolean addOrUpdate(TranslatedText translatedText);
    boolean delete(TranslatedText translatedText);
    void setOnItemsUpdateListener(OnItemsUpdateListener onItemsUpdateListener);
    void connect(Context context);
    boolean isConnected();
}
