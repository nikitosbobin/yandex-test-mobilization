package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class Direction_Tests {
    @Test(expected = NullPointerException.class)
    public void should_fail_if_from_is_null() {
        new Direction(null, new Language("ru"));
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_if_to_is_null() {
        new Direction(new Language("en"), null);
    }

    @Test
    public void parse_should_parse_directions() {
        Direction expectedDirection = new Direction(new Language("fr"), new Language("en"));

        Direction parsedDirection = Direction.parse("fr-en");

        assertEquals(expectedDirection, parsedDirection);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_should_fail_when_direction_format_is_not_supported() {
        Direction.parse("aaaaaa");
    }

    @Test(expected = NullPointerException.class)
    public void parse_should_fail_when_direction_null() {
        Direction.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_should_fail_when_direction_empty() {
        Direction.parse(Strings.empty);
    }

    @Test
    public void should_correctly_init_fields() {
        Direction direction = Direction.parse("fr-en");

        assertEquals(new Language("fr"), direction.getFrom());
        assertEquals(new Language("en"), direction.getTo());
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        Direction direction1 = Direction.parse("fr-en");
        Direction direction2 = Direction.parse("fr-en");

        boolean equality = direction1.equals(direction2);

        assertEquals(true, equality);
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        Direction direction1 = Direction.parse("fr-en");
        Direction direction2 = Direction.parse("fr-en");

        int direction1HashCode = direction1.hashCode();
        int direction2HashCode = direction2.hashCode();

        assertEquals(direction1HashCode, direction2HashCode);
    }
}