package com.nikit.bobin.wordstranslate.storage;

import android.content.Context;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.io.Closeable;

public interface ITranslationsDatabase extends Closeable {
    TranslatedText getById(long id);

    TranslatedText[] getAllTranslations(boolean orderDescending);

    TranslatedText[] getFavoriteTranslations(boolean orderDescending);

    boolean addOrUpdate(TranslatedText translatedText);

    boolean delete(TranslatedText translatedText);

    void deleteAllTranslations();

    void deleteAllFavorites();

    void addOnItemsUpdateListener(AbstractDatabaseOneTableContext.OnItemsUpdateListener onItemsUpdateListener);

    void connect(Context context);

    boolean isConnected();
}
