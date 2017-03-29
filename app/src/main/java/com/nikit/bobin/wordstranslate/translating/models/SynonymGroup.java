package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

import java.util.Arrays;

public class SynonymGroup {
    private String[] mean;
    private String[] synonyms;

    public SynonymGroup(String[] mean, String[] synonyms) {
        Ensure.notNull(mean, "mean");
        Ensure.notNull(synonyms, "synonyms");

        this.mean = mean;
        this.synonyms = synonyms;
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public String[] getMean() {
        return mean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynonymGroup group = (SynonymGroup) o;
        if (!Arrays.equals(mean, group.mean)) return false;
        return Arrays.equals(synonyms, group.synonyms);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mean);
        result = 31 * result + Arrays.hashCode(synonyms);
        return result;
    }
}
