package com.nikit.bobin.wordstranslate.history;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class InMemoryTranslationStorage implements IStorage<TranslatedText> {
    private ArrayList<TranslatedText> translations;
    private Runnable itemsUpdateListener;

    public InMemoryTranslationStorage() {
        translations = new ArrayList<>(32);
    }

    @Override
    public TranslatedText[] getSavedItems() {
        return translations.toArray(new TranslatedText[translations.size()]);
    }

    @Override
    public TranslatedText[] getSavedItemsReversed() {
        ArrayList<TranslatedText> list = new ArrayList<>(translations);
        Collections.reverse(list);
        return list.toArray(new TranslatedText[list.size()]);
    }

    @Override
    public boolean saveOrUpdateItem(TranslatedText translatedText) {
        if (translatedText == null || !translatedText.isSuccess())
            return false;
        translations.add(translatedText);
        if (itemsUpdateListener != null)
            itemsUpdateListener.run();
        return true;
    }

    @Override
    public int getCount() {
        return translations.size();
    }

    @Override
    public boolean delete(TranslatedText translatedText) {
        if (translations.remove(translatedText)) {
            if (itemsUpdateListener != null)
                itemsUpdateListener.run();
            return true;
        }
        return false;
    }

    @Override
    public void setOnItemsUpdateListener(Runnable onItemsUpdateListener) {
        itemsUpdateListener = onItemsUpdateListener;
    }
}
