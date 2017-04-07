package com.nikit.bobin.wordstranslate.storage;

import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.util.ArrayList;
import java.util.List;

public class TranslationsDatabase implements ITranslationsDatabase {
    private ArrayList<OnItemsUpdateListener> onItemsUpdateListeners;

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
                TranslatedText.listAll(TranslatedText.class, "ID " + (orderDescending ? "DESC" : "ASC"));
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
        if (TranslatedText.delete(translatedText)) {
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
            for (OnItemsUpdateListener listener
                    : onItemsUpdateListeners)
                listener.onDatabaseChange();
        }
    }

    @Override
    public void addOnItemsUpdateListener(OnItemsUpdateListener onItemsUpdateListener) {
        if (!onItemsUpdateListeners.contains(onItemsUpdateListener))
            onItemsUpdateListeners.add(onItemsUpdateListener);
    }
}
