package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class Translation_Tests {
    @Test(expected = NullPointerException.class)
    public void should_fail_if_text_is_null() {
        new Translation(null, Direction.parse("ru-en"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_text_is_empty() {
        new Translation("", Direction.parse("ru-en"));
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_if_direction_obj_is_null() {
        new Translation("text", (Direction) null);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_if_direction_str_is_null() {
        new Translation("text", (String) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_direction_str_is_empty() {
        new Translation("text", "");
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        Translation translation1 = new Translation("text", Direction.parse("ru-en"));
        Translation translation2 = new Translation("text", Direction.parse("ru-en"));

        assertTrue(translation1.equals(translation2));
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        Translation translation1 = new Translation("text", Direction.parse("ru-en"));
        Translation translation2 = new Translation("text", Direction.parse("ru-en"));

        int translation1HashCode = translation1.hashCode();
        int translation2HashCode = translation2.hashCode();

        assertEquals(translation1HashCode, translation2HashCode);
    }

    @Test
    public void getWordCount_should_return_words_count_in_text() {
        Translation translation = new Translation("original text", "en-ru");

        int wordCount = translation.getWordCount();

        assertEquals(2, wordCount);
    }

    @Test
    public void getWordCount_should_return_words_count_in_text_another_ctor() {
        Translation translation = new Translation("original text", Direction.parse("en-ru"));

        int wordCount = translation.getWordCount();

        assertEquals(2, wordCount);
    }

    @Test
    public void should_correctly_init_fields() {
        Translation translation = new Translation("original text", "en-ru");

        assertEquals("original text", translation.getOriginalText());
        assertEquals(Direction.parse("en-ru"), translation.getDirection());
    }
}