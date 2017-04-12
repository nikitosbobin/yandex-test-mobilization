package com.nikit.bobin.wordstranslate.storage;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

//Abstraction that provides some methods to store success translations information
public interface ITranslationsDatabase {
    TranslatedText getById(long id);

    TranslatedText[] getAllTranslations(boolean orderDescending);

    TranslatedText[] getFavoriteTranslations(boolean orderDescending);

    boolean save(TranslatedText translatedText);

    boolean delete(TranslatedText translatedText);

    void deleteAllTranslations();

    void deleteAllFavorites();

    void addOnItemsUpdateListener(OnTranslationDatabaseUpdateListener onItemsUpdateListener);
}
