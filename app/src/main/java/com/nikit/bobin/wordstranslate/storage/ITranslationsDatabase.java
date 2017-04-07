package com.nikit.bobin.wordstranslate.storage;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

public interface ITranslationsDatabase {
    TranslatedText getById(long id);

    TranslatedText[] getAllTranslations(boolean orderDescending);

    TranslatedText[] getFavoriteTranslations(boolean orderDescending);

    boolean save(TranslatedText translatedText);

    boolean delete(TranslatedText translatedText);

    void deleteAllTranslations();

    void deleteAllFavorites();

    void addOnItemsUpdateListener(OnItemsUpdateListener onItemsUpdateListener);
}
