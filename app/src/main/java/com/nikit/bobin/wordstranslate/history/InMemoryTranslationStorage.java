package com.nikit.bobin.wordstranslate.history;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class InMemoryTranslationStorage implements IStorage<TranslatedText> {
    private ArrayList<TranslatedText> translations;

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
        if (!translatedText.isSuccess())
            return false;
        translations.add(translatedText);
        return true;
    }

    @Override
    public int getCount() {
        return translations.size();
    }

    @Override
    public boolean delete(TranslatedText translatedText) {
        return translations.remove(translatedText);
    }
}
