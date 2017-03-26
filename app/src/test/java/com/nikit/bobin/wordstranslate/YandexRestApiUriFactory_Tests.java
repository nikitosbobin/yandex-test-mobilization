package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.YandexRestApiUriFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class YandexRestApiUriFactory_Tests {
    private YandexRestApiUriFactory factory;

    @Before
    public void setUp() {
        factory = new YandexRestApiUriFactory("appKey", "dictKey");
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_app_key_is_null() {
        new YandexRestApiUriFactory(null, "dictKey");
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_dict_key_is_null() {
        new YandexRestApiUriFactory("appKey", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_when_app_key_is_empty() {
        new YandexRestApiUriFactory("", "dictKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_when_dict_key_is_empty() {
        new YandexRestApiUriFactory("appKey", "");
    }

    @Test
    public void getLangs_should_return_correct_uri() {
        String expectedUrl = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=appKey";

        String actualUrl = factory.getLangs();

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void getLangs_should_return_correct_uri_with_ui() {
        String expectedUrl = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=appKey&ui=en";

        String actualUrl = factory.getLangs(new Language("en"));

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void translate_should_return_correct_uri() {
        String expectedUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=appKey&lang=en-ru&text=text";

        String actualUrl = factory.translate(Direction.parse("en-ru"), "text");

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void dictionaryLookup_should_return_correct_uri() {
        String expectedUrl = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dictKey&lang=en-ru&text=text";

        String actualUrl = factory.dictionaryLookup(Direction.parse("en-ru"), "text");

        assertEquals(expectedUrl, actualUrl);
    }

    @Test(expected = NullPointerException.class)
    public void translate_should_fail_when_direction_is_null() {
        factory.translate(null, "text");
    }
}