package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class Language extends SugarRecord {
    private Long id;
    private String key;
    private String title;
    private boolean isUi;

    // Don't use this constructor. It only for Sugar ORM usage.
    @Deprecated
    public Language() {
    }

    public Language(String key) {
        this(key, null);
    }

    public Language(String key, String title) {
        this(key, title, false);
    }

    public Language(String key, String title, boolean isUi) {
        Ensure.notNullOrEmpty(key, "key");

        this.key = key;
        this.title = title;
        this.isUi = isUi;
    }

    public Language asUiLanguage() {
        return new Language(key, title, true);
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        Ensure.notNullOrEmpty(title, "title");

        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return key.equals(language.key);
    }

    @Override
    public int hashCode() {
        if (key == null)
            return super.hashCode();
        return key.hashCode();
    }

    public boolean isUi() {
        return isUi;
    }

    @Override
    public Long getId() {
        return id;
    }
}
