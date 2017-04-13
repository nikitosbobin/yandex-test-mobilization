package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.helpers.Ensure;

import java.util.Arrays;

//Model describes some dictionary hints of some translation
public class WordLookup {
    private Translation translation;
    private SynonymGroup[] synonyms;

    public WordLookup(Translation translation, SynonymGroup[] synonyms) {
        Ensure.notNull(translation, "translation");
        Ensure.notNull(synonyms, "synonyms");

        this.translation = translation;
        this.synonyms = synonyms;
    }

    private WordLookup() {
    }

    public static WordLookup empty(Translation translation) {
        WordLookup wordLookup = new WordLookup();
        wordLookup.translation = translation;
        return wordLookup;
    }

    public boolean isEmpty() {
        return synonyms == null;
    }

    public Translation getTranslation() {
        return translation;
    }

    public SynonymGroup[] getSynonyms() {
        return synonyms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordLookup that = (WordLookup) o;
        if (translation != null ? !translation.equals(that.translation) : that.translation != null)
            return false;
        return Arrays.equals(synonyms, that.synonyms);
    }

    @Override
    public int hashCode() {
        int result = translation != null ? translation.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(synonyms);
        return result;
    }
}
