package com.nikit.bobin.wordstranslate.translating.models;

public class SynonymGroup {
    private String[] mean;
    private String[] synonyms;

    public SynonymGroup(String[] mean, String[] synonyms) {
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
