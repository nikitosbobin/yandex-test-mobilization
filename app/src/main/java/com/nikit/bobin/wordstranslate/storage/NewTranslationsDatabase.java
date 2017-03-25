package com.nikit.bobin.wordstranslate.storage;

import android.content.ContentValues;
import android.database.Cursor;

import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.util.ArrayList;

public class NewTranslationsDatabase
        extends AbstractDatabaseOneTableContext<TranslatedText>
        implements ITranslationsDatabase {
    private String[] columns = new String[] {"id", "translated", "original", "direction", "favorite"};

    public NewTranslationsDatabase() {
        super(
                "translations.db",
                "translations",
                "id integer primary key autoincrement, " +
                        "original text not null," +
                        "translated text not null," +
                        "direction text not null," +
                        "favorite integer default 0",
                "id");
    }

    @Override
    TranslatedText deserialize(Cursor cursor) {
        return TranslatedText.fromDatabase(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4) == 1);
    }

    @Override
    ContentValues serialize(TranslatedText translatedText) {
        ContentValues initialValues = new ContentValues();
        int id = translatedText.getId();
        if (id != -1) initialValues.put("id", id);
        initialValues.put("translated", translatedText.getTranslatedText());
        initialValues.put("original", translatedText.getTranslation().getOriginalText());
        initialValues.put("direction", translatedText.getTranslation().getDirection().toString());
        initialValues.put("favorite", translatedText.isFavorite() ? 1 : 0);
        return initialValues;
    }

    @Override
    public TranslatedText getById(long id) {
        ArrayList<TranslatedText> translations =
                extractAllData(columns, "id=?", new String[]{id + ""}, null);
        return translations.size() == 0 ? null : translations.get(0);
    }

    @Override
    public TranslatedText[] getAllTranslations(boolean orderDescending) {
        ArrayList<TranslatedText> translations = extractAllData(
                columns,
                null,
                null,
                "id " + (orderDescending ? "desc" : "asc"));
        return translations.toArray(new TranslatedText[translations.size()]);
    }

    @Override
    public int getAllTranslationsCount() {
        return (int) getCount(null, null);
    }

    @Override
    public TranslatedText[] getFavoriteTranslations(boolean orderDescending) {
        ArrayList<TranslatedText> translations = extractAllData(
                columns,
                "favorite=1",
                null,
                "id " + (orderDescending ? "desc" : "asc"));
        return translations.toArray(new TranslatedText[translations.size()]);
    }

    @Override
    public int getFavoriteTranslationsCount() {
        return (int) getCount("favorite=1", null);
    }

    @Override
    public boolean addOrUpdate(TranslatedText translatedText) {
        return insertOrUpdate(translatedText, true);
    }

    @Override
    public boolean delete(TranslatedText translatedText) {
        return delete("id=?", new String[]{translatedText.getId() + ""}, true);
    }

    @Override
    public void deleteAllTranslations() {
        clearTable(true);
    }

    @Override
    public void deleteAllFavorites() {
        delete("favorite=1", null, true);
    }
}
