package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.SynonymGroup;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class WordLookup_Tests {
    @Test(expected = NullPointerException.class)
    public void should_fail_when_translation_null() {
        SynonymGroup[] synonymGroups = new SynonymGroup[0];
        new WordLookup(null, synonymGroups);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_synonyms_null() {
        Translation translation = mock(Translation.class);
        new WordLookup(translation, null);
    }

    @Test
    public void should_correctly_init_fields() {
        Translation translation = mock(Translation.class);
        SynonymGroup[] synonymGroups = new SynonymGroup[0];
        WordLookup wordLookup = new WordLookup(translation, synonymGroups);

        assertSame(translation, wordLookup.getTranslation());
        assertSame(synonymGroups, wordLookup.getSynonyms());
    }

    @Test
    public void empty_should_create_empty_lookup() {
        WordLookup wordLookup = WordLookup.empty();

        assertNull(wordLookup.getTranslation());
        assertNull(wordLookup.getSynonyms());
    }

    @Test
    public void isEmpty_should_correctly_detect_empty_lookup() {
        WordLookup wordLookup = WordLookup.empty();

        assertTrue(wordLookup.isEmpty());
    }

    @Test
    public void isEmpty_should_correctly_detect_not_empty_lookup() {
        Translation translation = mock(Translation.class);
        SynonymGroup[] synonymGroups = new SynonymGroup[0];
        WordLookup wordLookup = new WordLookup(translation, synonymGroups);

        assertFalse(wordLookup.isEmpty());
    }
}
