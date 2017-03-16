package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class Language_Tests {
    private Language ruLang;

    @Before
    public void setUp() {
        ruLang = new Language("ru");
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_if_key_is_null() {
        new Language(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_key_is_empty() {
        new Language("");
    }

    @Test
    public void getKey_should_return_lang_key() {
        String key = ruLang.getKey();

        assertEquals("ru", key);
    }

    @Test
    public void getKey_should_correctly_detect_equality() {
        Language ruLang1 = new Language("ru");
        Language ruLang2 = new Language("ru");

        boolean equality = ruLang1.equals(ruLang2);

        assertEquals(true, equality);
    }

    @Test
    public void getKey_should_correctly_return_hash_code() {
        Language ruLang1 = new Language("ru");
        Language ruLang2 = new Language("ru");

        int ruLang1HashCode = ruLang1.hashCode();
        int ruLang2HashCode = ruLang2.hashCode();

        assertEquals(ruLang1HashCode, ruLang2HashCode);
    }

    @Test(expected = NullPointerException.class)
    public void addDirection_should_fail_when_direction_is_null() {
        ruLang.addDirection(null);
    }

    @Test(expected = NullPointerException.class)
    public void addDirection_should_fail_when_other_directions_is_null() {
        ruLang.addDirection(new Language("en"), null);
    }

    @Test
    public void should_save_and_detect_existed_direction() {
        ruLang.addDirection(new Language("en"));

        boolean directionExists = ruLang.hasDirection(new Language("en"));

        assertEquals(true, directionExists);
    }

    @Test
    public void should_not_detect_not_existed_direction() {
        boolean directionExists = ruLang.hasDirection(new Language("en"));

        assertEquals(false, directionExists);
    }

    @Test
    public void should_return_all_saved_directions() {
        ruLang.addDirection(new Language("en"));
        ruLang.addDirection(new Language("fr"));
        Language[] expectedDirections = new Language[] {new Language("en"), new Language("fr")};

        Language[] directions = ruLang.getDirections();

        Assert.assertArrayEquals(expectedDirections, directions);
    }

    @Test(expected = NullPointerException.class)
    public void addTitle_should_fail_when_lang_key_is_null() {
        ruLang.addTitle(null, "title");
    }

    @Test(expected = NullPointerException.class)
    public void addTitle_should_fail_when_title_is_null() {
        ruLang.addTitle(new Language("en"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTitle_should_fail_when_title_is_empty() {
        ruLang.addTitle(new Language("en"), "");
    }

    @Test(expected = NullPointerException.class)
    public void getTitle_should_fail_when_lang_key_is_null() {
        ruLang.getTitle(null);
    }

    @Test
    public void should_save_and_return_title_for_target_lang() {
        ruLang.addTitle(new Language("en"), "title");

        String title = ruLang.getTitle(new Language("en"));

        assertEquals("title", title);
    }

    @Test
    public void should_save_some_titles_and_return_title_for_target_lang() {
        ruLang.addTitle(new Language("en"), "titleEn");
        ruLang.addTitle(new Language("ru"), "titleRus");

        String title = ruLang.getTitle(new Language("en"));

        assertEquals("titleEn", title);
    }

    @Test
    public void should_return_empty_title_when_not_saved() {
        String title = ruLang.getTitle(new Language("en"));

        assertEquals(null, title);
    }
}