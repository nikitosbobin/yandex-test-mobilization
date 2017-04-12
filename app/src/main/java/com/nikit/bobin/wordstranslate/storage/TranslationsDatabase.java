package com.nikit.bobin.wordstranslate.storage;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.util.ArrayList;
import java.util.List;

//Implementation of ITranslationsDatabase. Uses Sugar ORM
public class TranslationsDatabase implements ITranslationsDatabase {
    private ArrayList<OnTranslationDatabaseUpdateListener> onItemsUpdateListeners;

    public TranslationsDatabase() {
        onItemsUpdateListeners = new ArrayList<>();
    }

    @Override
    public TranslatedText getById(long id) {
        return TranslatedText.findById(TranslatedText.class, id);
    }

    @Override
    public TranslatedText[] getAllTranslations(boolean orderDescending) {
        List<TranslatedText> translatedTexts =
                TranslatedText.listAll(
                        TranslatedText.class,
                        "ID " + (orderDescending ? "DESC" : "ASC"));
        return translatedTexts.toArray(new TranslatedText[translatedTexts.size()]);
    }

    @Override
    public TranslatedText[] getFavoriteTranslations(boolean orderDescending) {
        List<TranslatedText> translatedTexts = TranslatedText.find(
                TranslatedText.class,
                "IS_FAVORITE=?",
                new String[]{"1"},
                null,
                "ID " + (orderDescending ? "DESC" : "ASC"),
                null);
        return translatedTexts.toArray(new TranslatedText[translatedTexts.size()]);
    }

    @Override
    public boolean save(TranslatedText translatedText) {
        if (translatedText != null) {
            translatedText.save();
            notifyDataChanged();
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(TranslatedText translatedText) {
        if (translatedText != null && TranslatedText.delete(translatedText)) {
            notifyDataChanged();
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllTranslations() {
        if (TranslatedText.deleteAll(TranslatedText.class) > 0)
            notifyDataChanged();
    }

    @Override
    public void deleteAllFavorites() {
        if (TranslatedText.deleteAll(TranslatedText.class, "IS_FAVORITE=?", "1") > 0)
            notifyDataChanged();
    }

    private void notifyDataChanged() {
        if (onItemsUpdateListeners.size() != 0) {
            for (OnTranslationDatabaseUpdateListener listener
                    : onItemsUpdateListeners)
                listener.onTranslationDatabaseChange();
        }
    }

    @Override
    public void addOnItemsUpdateListener(OnTranslationDatabaseUpdateListener onItemsUpdateListener) {
        if (onItemsUpdateListener != null &&
                !onItemsUpdateListeners.contains(onItemsUpdateListener))
            onItemsUpdateListeners.add(onItemsUpdateListener);
    }
}
