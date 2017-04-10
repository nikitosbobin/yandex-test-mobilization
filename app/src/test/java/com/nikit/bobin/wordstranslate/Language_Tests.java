package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.helpers.Strings;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class Language_Tests {
    @Test(expected = NullPointerException.class)
    public void should_fail_if_key_is_null() {
        new Language(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_key_is_empty() {
        new Language("");
    }

    @Test
    public void equals_should_correctly_detect_equality() {
        Language ruLang1 = new Language("ru");
        Language ruLang2 = new Language("ru");

        assertTrue(ruLang1.equals(ruLang2));
    }

    @Test
    public void hashCode_should_correctly_return_hash_code() {
        Language ruLang1 = new Language("ru");
        Language ruLang2 = new Language("ru");

        int ruLang1HashCode = ruLang1.hashCode();
        int ruLang2HashCode = ruLang2.hashCode();

        assertEquals(ruLang1HashCode, ruLang2HashCode);
    }

    @Test
    public void setTitle_should_correctly_set_title() {
        Language language = new Language("ru");

        language.setTitle("title");

        assertEquals("title", language.getTitle());
    }

    @Test(expected = NullPointerException.class)
    public void setTitle_should_fail_when_title_null() {
        Language language = new Language("ru");

        language.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTitle_should_fail_when_title_empty() {
        Language language = new Language("ru");

        language.setTitle(Strings.empty);
    }
}