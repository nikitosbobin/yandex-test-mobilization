package com.nikit.bobin.wordstranslate.history;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.util.ArrayList;
import java.util.Collections;

public class InMemoryTranslationsDatabase implements ITranslationsDatabase {
    private ArrayList<TranslatedText> translations;
    private Runnable onItemsUpdateListener;

    public InMemoryTranslationsDatabase() {
        translations = new ArrayList<>();
    }

    @Override
    public TranslatedText[] getAllTranslations(boolean orderDescending) {
        if (orderDescending) {
            ArrayList<TranslatedText> list = new ArrayList<>(translations);
            Collections.reverse(list);
            return list.toArray(new TranslatedText[list.size()]);
        } else {
            return translations.toArray(new TranslatedText[translations.size()]);
        }
    }

    @Override
    public int getAllTranslationsCount() {
        return translations.size();
    }

    @Override
    public TranslatedText[] getFavoriteTranslations(boolean orderDescending) {
        ArrayList<TranslatedText> list = new ArrayList<>(translations);
        for (TranslatedText t : translations)
            if (t.isFavorite())
                list.add(t);
        if (orderDescending)
            Collections.reverse(list);
        return list.toArray(new TranslatedText[list.size()]);
    }

    @Override
    public int getFavoriteTranslationsCount() {
        return getFavoriteTranslations(false).length;
    }

    @Override
    public boolean addOrUpdate(TranslatedText translatedText) {
        if (translatedText == null || !translatedText.isSuccess())
            return false;
        translations.add(translatedText);
        if (onItemsUpdateListener != null)
            onItemsUpdateListener.run();
        return true;
    }

    @Override
    public boolean delete(TranslatedText translatedText) {
        if (translatedText == null)
            return false;
        if (translations.remove(translatedText)) {
            if (onItemsUpdateListener != null)
                onItemsUpdateListener.run();
            return true;
        }
        return false;
    }

    @Override
    public void setOnItemsUpdateListener(Runnable onItemsUpdateListener) {
        this.onItemsUpdateListener = onItemsUpdateListener;
    }
}
