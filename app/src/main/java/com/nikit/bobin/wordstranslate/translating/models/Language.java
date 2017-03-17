package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

import java.util.HashSet;

//todo: to struct
public class Language {
    private String key;
    private String title;
    private HashSet<Language> directions;

    public Language(String key, String title) {
        Ensure.notNullOrEmpty(key, "key");

        this.key = key;
        this.title = title;
        directions = new HashSet<>();
    }

    public Language(String key) {
        this(key, null);
    }

    public void addDirection(Language direction, Language... otherDirections) {
        Ensure.notNull(direction, "direction");
        Ensure.notNull(otherDirections, "otherDirections");

        directions.add(direction);

        for (Language dir : otherDirections)
            directions.add(dir);
    }

    public boolean hasDirection(Language direction) {
        return directions.contains(direction);
    }

    public Language[] getDirections() {
        return directions.toArray(new Language[directions.size()]);
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
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
}
