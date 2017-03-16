package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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
    public void should_fail_if_direction_is_null() {
        new Translation("text", null);
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        Translation translation1 = new Translation("text", Direction.parse("ru-en"));
        Translation translation2 = new Translation("text", Direction.parse("ru-en"));

        boolean equality = translation1.equals(translation2);

        assertEquals(true, equality);
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        Translation translation1 = new Translation("text", Direction.parse("ru-en"));
        Translation translation2 = new Translation("text", Direction.parse("ru-en"));

        int translation1HashCode = translation1.hashCode();
        int translation2HashCode = translation2.hashCode();

        assertEquals(translation1HashCode, translation2HashCode);
    }
}