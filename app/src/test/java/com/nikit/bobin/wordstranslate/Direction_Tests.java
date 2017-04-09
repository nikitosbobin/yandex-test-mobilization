package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
    public void parseKeySerialized_should_parse_directions() {
        Direction expectedDirection = new Direction(new Language("fr"), new Language("en"));

        Direction parsedDirection = Direction.parseKeySerialized("fr-en");

        assertEquals(expectedDirection, parsedDirection);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseKeySerialized_should_fail_when_direction_format_is_not_supported() {
        Direction.parseKeySerialized("aaaaaa");
    }

    @Test(expected = NullPointerException.class)
    public void parseKeySerialized_should_fail_when_direction_null() {
        Direction.parseKeySerialized(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseKeySerialized_should_fail_when_direction_empty() {
        Direction.parseKeySerialized(Strings.empty);
    }

    @Test
    public void should_correctly_init_fields() {
        Direction direction = Direction.parseKeySerialized("fr-en");

        assertEquals(new Language("fr"), direction.getFrom());
        assertEquals(new Language("en"), direction.getTo());
        assertEquals("fr-en", direction.toString());
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        Direction direction1 = Direction.parseKeySerialized("fr-en");
        Direction direction2 = Direction.parseKeySerialized("fr-en");

        assertTrue(direction1.equals(direction2));
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        Direction direction1 = Direction.parseKeySerialized("fr-en");
        Direction direction2 = Direction.parseKeySerialized("fr-en");

        int direction1HashCode = direction1.hashCode();
        int direction2HashCode = direction2.hashCode();

        assertEquals(direction1HashCode, direction2HashCode);
    }

    @Test
    public void fullSerialize_should_correctly_serialize_direction() {
        Direction direction = new Direction(
                new Language("ru", "Russian"),
                new Language("en", "English"));

        String serialization = direction.fullSerialize();

        assertEquals("ru|en|Russian|English", serialization);
    }

    @Test
    public void fullSerialize_should_correctly_serialize_direction_without_titles() {
        Direction direction = new Direction(
                new Language("ru"),
                new Language("en"));

        String serialization = direction.fullSerialize();

        assertEquals("ru|en||", serialization);
    }

    @Test
    public void fullSerialize_should_correctly_serialize_direction_without_one_of_titles() {
        Direction direction = new Direction(
                new Language("ru"),
                new Language("en", "English"));

        String serialization = direction.fullSerialize();

        assertEquals("ru|en||English", serialization);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseFullSerialized_should_fail_when_direction_format_is_not_supported() {
        Direction.parseFullSerialized("aaaaaa");
    }

    @Test
    public void parseFullSerialized_should_correctly_deserialize_correct_string() {
        Direction expectedDirection = new Direction(
                new Language("ru", "Russian"),
                new Language("en", "English"));

        Direction actualDirection = Direction.parseFullSerialized("ru|en|Russian|English");

        assertEquals(expectedDirection, actualDirection);
    }

    @Test
    public void parseFullSerialized_should_correctly_deserialize_when_first_title_empty() {
        Direction expectedDirection = new Direction(
                new Language("ru"),
                new Language("en", "English"));

        Direction actualDirection = Direction.parseFullSerialized("ru|en||English");

        assertEquals(expectedDirection, actualDirection);
    }

    @Test
    public void parseFullSerialized_should_correctly_deserialize_when_second_title_empty() {
        Direction expectedDirection = new Direction(
                new Language("ru", "Russian"),
                new Language("en"));

        Direction actualDirection = Direction.parseFullSerialized("ru|en|Russian|");

        assertEquals(expectedDirection, actualDirection);
    }

    @Test
    public void parseFullSerialized_should_correctly_deserialize_when_all_titles_empty() {
        Direction expectedDirection = new Direction(
                new Language("ru"),
                new Language("en"));

        Direction actualDirection = Direction.parseFullSerialized("ru|en||");

        assertEquals(expectedDirection, actualDirection);
    }
}