package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

import java.util.HashMap;
import java.util.HashSet;

//todo: to struct
public class Language {
    private String key;
    private HashMap<Language, String> titles;
    private HashSet<Language> directions;

    public Language(String key) {
        Ensure.notNullOrEmpty(key, "key");

        this.key = key;
        titles = new HashMap<>();
        directions = new HashSet<>();
    }

    public void addTitle(Language langKey, String title) {
        Ensure.notNullOrEmpty(title, "title");
        Ensure.notNull(langKey, "langKey");

        titles.put(langKey, title);
    }

    public String getTitle(Language langKey) {
        Ensure.notNull(langKey, "langKey");

        if (titles.containsKey(langKey))
            return titles.get(langKey);

        return null;
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
