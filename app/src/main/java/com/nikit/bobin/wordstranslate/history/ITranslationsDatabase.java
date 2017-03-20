package com.nikit.bobin.wordstranslate.history;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

public interface ITranslationsDatabase {
    TranslatedText[] getAllTranslations(boolean orderDescending);
    int getAllTranslationsCount();
    TranslatedText[] getFavoriteTranslations(boolean orderDescending);
    int getFavoriteTranslationsCount();
    boolean addOrUpdate(TranslatedText translatedText);
    boolean delete(TranslatedText translatedText);
    void setOnItemsUpdateListener(Runnable onItemsUpdateListener);
}
