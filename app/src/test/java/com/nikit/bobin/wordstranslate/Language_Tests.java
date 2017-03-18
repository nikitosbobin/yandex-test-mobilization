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
        ruLang = new Language("ru", "title");
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
    public void equals_should_correctly_detect_equality() {
        Language ruLang1 = new Language("ru");
        Language ruLang2 = new Language("ru");

        boolean equality = ruLang1.equals(ruLang2);

        assertEquals(true, equality);
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        Language ruLang1 = new Language("ru");
        Language ruLang2 = new Language("ru");

        int ruLang1HashCode = ruLang1.hashCode();
        int ruLang2HashCode = ruLang2.hashCode();

        assertEquals(ruLang1HashCode, ruLang2HashCode);
    }
}