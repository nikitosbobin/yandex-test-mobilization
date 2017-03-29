package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

public class WordLookup {
    private Translation translation;
    private SynonymGroup[] synonyms;

    public WordLookup(Translation translation, SynonymGroup[] synonyms) {
        Ensure.notNull(translation, "translation");
        Ensure.notNull(synonyms, "synonyms");

        this.translation = translation;
        this.synonyms = synonyms;
    }

    private WordLookup(){}

    public static WordLookup empty() {
        return new WordLookup();
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
