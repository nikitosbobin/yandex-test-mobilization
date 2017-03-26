package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

import java.util.HashSet;
// refactored
// tested
public class Language implements Comparable<Language>{
    private String key;
    private String title;

    public Language(String key, String title) {
        Ensure.notNullOrEmpty(key, "key");

        this.key = key;
        this.title = title;
    }

    public Language(String key) {
        this(key, null);
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
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
        return key.hashCode();
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public int compareTo(Language o) {
        if (title != null && o.title != null)
            return title.compareTo(o.title);
        return key.compareTo(o.key);
    }
}
