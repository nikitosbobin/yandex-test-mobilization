package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class TranslatedText_Tests {
    private Translation translation;

    @Before
    public void setUp() {
        translation = new Translation("original", "fr-en");
    }

    @Test(expected = IllegalArgumentException.class)
    public void success_should_fail_when_text_empty() {
        TranslatedText.success(Strings.empty, translation);
    }

    @Test(expected = NullPointerException.class)
    public void success_should_fail_when_text_null() {
        TranslatedText.success(null, translation);
    }

    @Test(expected = NullPointerException.class)
    public void success_should_fail_when_translation_null() {
        TranslatedText.success("text", null);
    }

    @Test
    public void success_should_correctly_init_fields() {
        TranslatedText translated = TranslatedText.success("translated", translation);

        assertEquals("translated", translated.getTranslatedText());
        assertEquals(Long.valueOf(-1), translated.getId());
        assertSame(translation, translated.getTranslation());
        assertTrue(translated.isSuccess());
        assertFalse(translated.isFavorite());
    }

    @Test(expected = NullPointerException.class)
    public void fail_should_fail_when_translation_null() {
        TranslatedText.fail(null);
    }

    @Test
    public void fail_should_correctly_init_fields() {
        TranslatedText translated = TranslatedText.fail(translation);

        assertEquals(null, translated.getTranslatedText());
        assertEquals(Long.valueOf(-1), translated.getId());
        assertSame(translation, translated.getTranslation());
        assertFalse(translated.isSuccess());
        assertFalse(translated.isFavorite());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDatabase_should_fail_when_translated_text_empty() {
        TranslatedText.fromDatabase(0, Strings.empty, "original", "en-fr", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDatabase_should_fail_when_original_text_empty() {
        TranslatedText.fromDatabase(0, "text", Strings.empty, "en-fr", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDatabase_should_fail_when_direction_empty() {
        TranslatedText.fromDatabase(0, "text", "original", Strings.empty, true);
    }

    @Test
    public void fromDatabase_should_correctly_init_fields() {
        TranslatedText translated = TranslatedText.fromDatabase(123, "text", "original", "fr-en", true);

        assertEquals("text", translated.getTranslatedText());

        assertEquals(Long.valueOf(123), translated.getId());
        assertEquals(translation, translated.getTranslation());
        assertTrue(translated.isSuccess());
        assertTrue(translated.isFavorite());
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        TranslatedText translated1 = TranslatedText.success("translated", translation);
        TranslatedText translated2 = TranslatedText.success("translated", translation);

        assertTrue(translated1.equals(translated2));
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        TranslatedText translated1 = TranslatedText.success("translated", translation);
        TranslatedText translated2 = TranslatedText.success("translated", translation);

        int translated1HashCode = translated1.hashCode();
        int translated2HashCode = translated2.hashCode();

        assertEquals(translated1HashCode, translated2HashCode);
    }

    @Test
    public void changeFavoriteState_should_not_create_new_instance_with_changed_favorite_state() {
        TranslatedText translated = TranslatedText.success("translated", translation);

        TranslatedText newTranslated = translated.setFavorite(true);

        assertTrue(newTranslated.isFavorite());
        assertSame(translated, newTranslated);
        assertTrue(newTranslated.isSuccess());
        assertEquals("translated", newTranslated.getTranslatedText());
        assertSame(translation, newTranslated.getTranslation());
        assertEquals("original", newTranslated.getTranslation().getOriginalText());
        assertEquals("fr-en", newTranslated.getTranslation().getDirection().toString());
    }
}
