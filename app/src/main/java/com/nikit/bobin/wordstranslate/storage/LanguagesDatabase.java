package com.nikit.bobin.wordstranslate.storage;

import android.content.ContentValues;
import android.database.Cursor;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.util.ArrayList;
import java.util.Locale;

public class LanguagesDatabase
        extends AbstractDatabaseOneTableContext<Language>
        implements ILanguagesDatabase {
    private Language ui;
    private String[] columns = new String[]{"key", "title", "is_ui"};

    public LanguagesDatabase() {
        super(
                "languages.db",
                "languages",
                "id integer primary key autoincrement, " +
                        "key text not null," +
                        "title text not null," +
                        "is_ui integer default 0",
                "id");
        ui = new Language(Locale.getDefault().getLanguage());
    }

    @Override
    Language deserialize(Cursor cursor) {
        return new Language(cursor.getString(0), cursor.getString(1));
    }

    @Override
    ContentValues serialize(Language language) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("key", language.getKey());
        initialValues.put("title", language.getTitle());
        initialValues.put("is_ui", language.equals(ui) ? 1 : 0);
        return initialValues;
    }

    @Override
    public Language getLanguage(String languageKey, Language ui) {
        if (languageKey != null && isLanguagesSaved(ui)) {
            ArrayList<Language> languages =
                    extractAllData(columns, "key=?", new String[]{languageKey}, null);
            if (languages.size() > 0)
                return languages.get(0);
        }
        return null;
    }

    @Override
    public Language[] getLanguages(boolean orderDescending) {
        ArrayList<Language> languages = extractAllData(
                columns,
                null,
                null,
                "title " + (orderDescending ? "desc" : "asc"));
        return languages.toArray(new Language[languages.size()]);
    }

    @Override
    public boolean replaceLanguages(Language[] languages, Language ui) {
        if (isLanguagesSaved(ui))
            return false;
        clearTable(false);
        for (Language l : languages) {
            add(l, false);
        }
        return true;
    }

    @Override
    public boolean isLanguagesSaved(Language ui) {
        return getCount("key=? and is_ui=1", new String[]{ui.getKey()}) != 0;
    }
}
