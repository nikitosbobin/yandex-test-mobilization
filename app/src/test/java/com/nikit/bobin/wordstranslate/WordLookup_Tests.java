package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.SynonymGroup;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
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
        WordLookup wordLookup = WordLookup.empty(null);

        assertNull(wordLookup.getTranslation());
        assertNull(wordLookup.getSynonyms());
    }

    @Test
    public void isEmpty_should_correctly_detect_empty_lookup() {
        WordLookup wordLookup = WordLookup.empty(null);

        assertTrue(wordLookup.isEmpty());
    }

    @Test
    public void isEmpty_should_correctly_detect_not_empty_lookup() {
        Translation translation = mock(Translation.class);
        SynonymGroup[] synonymGroups = new SynonymGroup[0];
        WordLookup wordLookup = new WordLookup(translation, synonymGroups);

        assertFalse(wordLookup.isEmpty());
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        Translation translation1 = new Translation("text", "en-fr");
        Translation translation2 = new Translation("text", "en-fr");
        SynonymGroup[] synonymGroups1 = new SynonymGroup[]{
                new SynonymGroup(new String[]{"abc", "def"}, new String[]{"ghi", "jkl"}),
                new SynonymGroup(new String[]{"nop", "qrs"}, new String[]{"tuv", "wxy"})
        };
        SynonymGroup[] synonymGroups2 = new SynonymGroup[]{
                new SynonymGroup(new String[]{"abc", "def"}, new String[]{"ghi", "jkl"}),
                new SynonymGroup(new String[]{"nop", "qrs"}, new String[]{"tuv", "wxy"})
        };

        WordLookup wordLookup1 = new WordLookup(translation1, synonymGroups1);
        WordLookup wordLookup2 = new WordLookup(translation2, synonymGroups2);

        assertTrue(wordLookup1.equals(wordLookup2));
    }

    @Test
    public void hashCode_should_calculate_correctly_hash_code() {
        Translation translation1 = new Translation("text", "en-fr");
        Translation translation2 = new Translation("text", "en-fr");
        SynonymGroup[] synonymGroups1 = new SynonymGroup[]{
                new SynonymGroup(new String[]{"abc", "def"}, new String[]{"ghi", "jkl"}),
                new SynonymGroup(new String[]{"nop", "qrs"}, new String[]{"tuv", "wxy"})
        };
        SynonymGroup[] synonymGroups2 = new SynonymGroup[]{
                new SynonymGroup(new String[]{"abc", "def"}, new String[]{"ghi", "jkl"}),
                new SynonymGroup(new String[]{"nop", "qrs"}, new String[]{"tuv", "wxy"})
        };

        WordLookup wordLookup1 = new WordLookup(translation1, synonymGroups1);
        WordLookup wordLookup2 = new WordLookup(translation2, synonymGroups2);

        assertEquals(wordLookup1.hashCode(), wordLookup2.hashCode());
    }
}
