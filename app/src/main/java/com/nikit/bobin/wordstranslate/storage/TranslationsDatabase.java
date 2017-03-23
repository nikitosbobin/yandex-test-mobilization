package com.nikit.bobin.wordstranslate.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nikit.bobin.wordstranslate.functional.OnItemsUpdateListener;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import java.io.IOException;
import java.util.ArrayList;


public class TranslationsDatabase implements ITranslationsDatabase {
    private final String DB_NAME = "translations.db";
    private final String TRANSLATIONS_TABLE_NAME = "translations";
    private OnItemsUpdateListener onItemsUpdateListener;
    private SQLiteDatabase database;

    private Integer count;
    private Integer favoriteCount;
    private TranslatedText[] allTranslations;
    private TranslatedText[] favoriteTranslations;

    public void connect(Context context) {
        database = getDataBase(context);
    }

    @Override
    public TranslatedText getById(long id) {
        Cursor cursor = database.rawQuery(
                String.format("select id, translated, original, direction, favorite from %s where id=%d",
                        TRANSLATIONS_TABLE_NAME, id), null);
        ArrayList<TranslatedText> list = readAllTranslations(cursor);
        TranslatedText result = null;
        if (list.size() > 0)
            result = list.get(0);
        cursor.close();
        return result;
    }

    @Override
    public TranslatedText[] getAllTranslations(boolean orderDescending) {
        if (allTranslations != null)
            return allTranslations;
        Log.d("logs", "getAllTranslations");
        Cursor cursor = database.rawQuery(
                String.format("select id, translated, original, direction, favorite from %s order by id %s",
                        TRANSLATIONS_TABLE_NAME, orderDescending ? "desc" : "asc"), null);
        ArrayList<TranslatedText> list = readAllTranslations(cursor);
        cursor.close();
        allTranslations = list.toArray(new TranslatedText[list.size()]);
        return allTranslations;
    }

    @Override
    public int getAllTranslationsCount() {
        if (count != null)
            return count;
        Log.d("logs", "getAllTranslationsCount");
        Cursor cursor = database.rawQuery(String.format("select count(*) from %s", TRANSLATIONS_TABLE_NAME), null);
        int result = 0;
        if (cursor.moveToFirst())
            result = cursor.getInt(0);
        cursor.close();
        count = result;
        return result;
    }

    @Override
    public TranslatedText[] getFavoriteTranslations(boolean orderDescending) {
        if (favoriteTranslations != null)
            return favoriteTranslations;
        Log.d("logs", "getFavoriteTranslations");
        Cursor cursor = database.rawQuery(
                String.format("select id, translated, original, direction, favorite from %s where favorite=1" +
                        " order by id %s",
                        TRANSLATIONS_TABLE_NAME, orderDescending ? "desc" : "asc"), null);
        ArrayList<TranslatedText> list = readAllTranslations(cursor);
        cursor.close();
        favoriteTranslations = list.toArray(new TranslatedText[list.size()]);
        return favoriteTranslations;
    }

    @Override
    public int getFavoriteTranslationsCount() {
        if (favoriteCount != null)
            return favoriteCount;
        Log.d("logs", "getFavoriteTranslationsCount");
        Cursor cursor = database.rawQuery(
                String.format("select count(*) from %s where favorite=1", TRANSLATIONS_TABLE_NAME), null);
        int result = 0;
        if (cursor.moveToFirst())
            result = cursor.getInt(0);
        cursor.close();
        favoriteCount = result;
        return result;
    }

    @Override
    public boolean addOrUpdate(TranslatedText translatedText) {
        ContentValues initialValues = new ContentValues();
        int id = translatedText.getId();
        if (id != -1)
            initialValues.put("id", id);
        initialValues.put("translated", translatedText.getTranslatedText());
        initialValues.put("original", translatedText.getTranslation().getOriginalText());
        initialValues.put("direction", translatedText.getTranslation().getDirection().toString());
        initialValues.put("favorite", translatedText.isFavorite() ? 1 : 0);

        long inserted = database.insertWithOnConflict(
                TRANSLATIONS_TABLE_NAME,
                null,
                initialValues,
                SQLiteDatabase.CONFLICT_IGNORE);
        if (inserted == -1) {
            int update = database.update(
                    TRANSLATIONS_TABLE_NAME,
                    initialValues,
                    "id=?",
                    new String[]{id + ""});
            boolean updated = update != 0;
            if (updated) {
                clearCache();
                if (onItemsUpdateListener != null)
                    onItemsUpdateListener.onDatabaseChange();
            }
            return updated;
        }
        if (onItemsUpdateListener != null)
            onItemsUpdateListener.onDatabaseChange();
        clearCache();
        return true;
    }

    @Override
    public boolean delete(TranslatedText translatedText) {
        int deleted = database.delete(
                TRANSLATIONS_TABLE_NAME,
                "id=?",
                new String[]{translatedText.getId() + ""});
        boolean anyDeleted = deleted != 0;
        if (anyDeleted) {
            if (onItemsUpdateListener != null)
                onItemsUpdateListener.onDatabaseChange();
            clearCache();
        }
        return anyDeleted;
    }

    @Override
    public void deleteAllTranslations() {
        if (database.delete(TRANSLATIONS_TABLE_NAME, null, null) > 0) {
            clearCache();
            if (onItemsUpdateListener != null)
                onItemsUpdateListener.onDatabaseChange();
        }
    }

    @Override
    public void deleteAllFavorites() {
        if (database.delete(TRANSLATIONS_TABLE_NAME, "favorite=?", new String[] {1 + ""}) > 0) {
            clearCache();
            if (onItemsUpdateListener != null)
                onItemsUpdateListener.onDatabaseChange();
        }
    }

    @Override
    public void setOnItemsUpdateListener(OnItemsUpdateListener onItemsUpdateListener) {
        this.onItemsUpdateListener = onItemsUpdateListener;
    }

    private SQLiteDatabase getDataBase(Context context) {
        String createTableQuery = String.format("create table if not exists %s (" +
                "id integer primary key autoincrement, " +
                "original text not null," +
                "translated text not null," +
                "direction text not null," +
                "favorite integer default 0)", TRANSLATIONS_TABLE_NAME);
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL(createTableQuery);
        return sqLiteDatabase;
    }

    private ArrayList<TranslatedText> readAllTranslations(Cursor cursor) {
        if (cursor.moveToFirst()) {
            ArrayList<TranslatedText> result = new ArrayList<>();
            do {
                TranslatedText text = TranslatedText.fromDatabase(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) == 1);
                result.add(text);
            } while (cursor.moveToNext());
            return result;
        }
        return new ArrayList<>(0);
    }

    public boolean isConnected() {
        return database != null && database.isOpen();
    }

    @Override
    public void close() throws IOException {
        if (isConnected()) {
            database.close();
        }
    }

    private void clearCache() {
        count = null;
        favoriteCount = null;
        allTranslations = null;
        favoriteTranslations = null;
    }
}
