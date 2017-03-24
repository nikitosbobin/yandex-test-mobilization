package com.nikit.bobin.wordstranslate.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.io.IOException;
import java.util.ArrayList;

public class LanguagesDatabase implements ILanguagesDatabase {
    private SQLiteDatabase database;
    private final String DB_NAME = "languages.db";
    private final String LANGUAGES_TABLE_NAME = "languages";

    @Override
    public Language getLanguage(Language language, Language ui) {
        if (language != null && isLanguagesSaved(ui)) {
            Cursor cursor = database.rawQuery(
                    String.format("select title from %s where key='%s'",
                            LANGUAGES_TABLE_NAME, language.getKey()),
                    null);
            if (cursor.moveToFirst()) {
                String result = cursor.getString(0);
                cursor.close();
                return new Language(language.getKey(), result);
            }
        }
        return null;
    }

    @Override
    public Language[] getLanguages(boolean orderDescending) {
        Cursor cursor = database.rawQuery(
                String.format("select key, title from %s order by title %s",
                        LANGUAGES_TABLE_NAME, orderDescending ? "desc" : "asc"),
                null);
        ArrayList<Language> languages = readAllLanguages(cursor);
        cursor.close();
        return languages.toArray(new Language[languages.size()]);
    }

    @Override
    public int getLanguagesCount() {
        Cursor cursor = database.rawQuery(
                String.format("select count(*) from %s", LANGUAGES_TABLE_NAME),
                null);
        int result = 0;
        if (cursor.moveToFirst())
            result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    @Override
    public boolean replaceLanguages(Language[] languages, Language ui) {
        if (isLanguagesSaved(ui))
            return false;
        database.delete(LANGUAGES_TABLE_NAME, null, null);
        for (Language l : languages) {
            ContentValues initialValues = new ContentValues();
            initialValues.put("key", l.getKey());
            initialValues.put("title", l.getTitle());
            initialValues.put("is_ui", l.equals(ui) ? 1 : 0);
            database.insert(LANGUAGES_TABLE_NAME, null, initialValues);
        }
        return true;
    }

    @Override
    public boolean isLanguagesSaved(Language ui) {
        Cursor cursor = database.rawQuery(
                String.format("select count(*) from %s where key='%s' and is_ui=1", LANGUAGES_TABLE_NAME, ui.getKey()),
                null);
        int result = 0;
        if (cursor.moveToFirst())
            result = cursor.getInt(0);
        cursor.close();
        return result != 0;
    }

    @Override
    public void connect(Context context) {
        database = getDataBase(context);
    }

    @Override
    public boolean isConnected() {
        return database != null && database.isOpen();
    }

    @Override
    public void close() throws IOException {
        if (isConnected()) {
            database.close();
        }
    }

    private ArrayList<Language> readAllLanguages(Cursor cursor) {
        if (cursor.moveToFirst()) {
            ArrayList<Language> result = new ArrayList<>();
            do {
                result.add(new Language(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
            return result;
        }
        return new ArrayList<>(0);
    }

    private SQLiteDatabase getDataBase(Context context) {
        String createTableQuery = String.format("create table if not exists %s (" +
                "id integer primary key autoincrement, " +
                "key text not null," +
                "title text not null," +
                "is_ui integer default 0)", LANGUAGES_TABLE_NAME);
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL(createTableQuery);
        return sqLiteDatabase;
    }
}
