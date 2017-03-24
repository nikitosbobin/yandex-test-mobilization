package com.nikit.bobin.wordstranslate.translating.models;

public class WordLookup {
    private Translation translation;
    private SynonymGroup[] synonyms;

    public WordLookup(Translation translation, SynonymGroup[] synonyms) {
        this.translation = translation;
        this.synonyms = synonyms;
    }

    public static WordLookup empty() {
        return new WordLookup(null, null);
    }

    public boolean isEmpty() {
        return translation == null && synonyms == null;
    }

    public Translation getTranslation() {
        return translation;
    }

    public SynonymGroup[] getSynonyms() {
        return synonyms;
    }
}
