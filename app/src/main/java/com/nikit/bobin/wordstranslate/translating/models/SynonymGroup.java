package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

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
}
