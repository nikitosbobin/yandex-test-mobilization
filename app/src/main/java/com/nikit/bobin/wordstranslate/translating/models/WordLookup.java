package com.nikit.bobin.wordstranslate.translating.models;

public class WordLookup {
    private Translation translation;
    private SynonymGroup[] synonyms;

    public WordLookup(Translation translation, SynonymGroup[] synonyms) {
        this.translation = translation;
        this.synonyms = synonyms;
    }

    public Translation getTranslation() {
        return translation;
    }

    public SynonymGroup[] getSynonyms() {
        return synonyms;
    }
}
