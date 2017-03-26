package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.YandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.junit.Before;
import org.junit.Test;

import okhttp3.Response;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class YandexResponseExtractor_Tests {
    private YandexResponseExtractor extractor;

    @Before
    public void setUp() {
        extractor = new YandexResponseExtractor();
    }

    @Test(expected = NullPointerException.class)
    public void extractSupportedLanguages_should_fail_when_response_is_null() throws Exception {
        extractor.extractLanguages(null);
    }

    @Test(expected = NullPointerException.class)
    public void extractTranslation_should_fail_when_response_is_null() throws Exception {
        extractor.extractTranslation(null, new Translation("text", "en-ru"));
    }

    @Test(expected = NullPointerException.class)
    public void extractTranslation_should_fail_when_translation_is_null() throws Exception {
        extractor.extractTranslation(TestData.createEmptyResponse(), null);
    }

    @Test(expected = RuntimeException.class)
    public void extractSupportedLanguages_should_fail_when_response_is_not_success() throws Exception{
        extractor.extractLanguages(TestData.createFakeResponse(400, "", "http://fake"));
    }

    @Test(expected = RuntimeException.class)
    public void extractTranslation_should_fail_when_response_is_not_success() throws Exception {
        Translation translation = new Translation("text", "en-ru");

        extractor.extractTranslation(TestData.createFakeResponse(400, "{}", "http://fake"), translation);
    }

    @Test
    public void extractTranslation_should_extract_translation_from_correct_response() throws Exception {
        String translationJson = TestData.createTranslationJson("ru-en", "time");
        Response fakeResponse = TestData.createFakeResponse(200, translationJson, "http://do-translate/");
        Translation translation = new Translation("время", Direction.parse("ru-en"));
        TranslatedText expectedTranslatedText = TranslatedText.success("time", translation);
        TranslatedText translatedText = extractor.extractTranslation(fakeResponse, translation);

        assertEquals(expectedTranslatedText, translatedText);
    }

    @Test
    public void extractTranslation_should_return_failed_translation_from_empty_response() throws Exception {
        String translationJson = TestData.createTranslationJson("en-ru", null);
        Response fakeResponse = TestData.createFakeResponse(200, translationJson, "http://do-translate/");
        Translation translation = new Translation("text", "en-ru");
        TranslatedText expectedTranslatedText = TranslatedText.fail(translation);

        TranslatedText translatedText = extractor.extractTranslation(fakeResponse, translation);

        assertEquals(expectedTranslatedText, translatedText);
    }

    @Test(expected = RuntimeException.class)
    public void extractTranslation_should_fail_when_yandex_response_code_in_not_successful() throws Exception {
        String translationJson = TestData.createFailJson(500);
        Response fakeResponse = TestData.createFakeResponse(200, translationJson, "http://do-translate/");
        Translation translation = new Translation("text", "en-ru");

        extractor.extractTranslation(fakeResponse, translation);
    }

    @Test(expected = RuntimeException.class)
    public void extractTranslation_should_fail_when_yandex_response_is_not_json() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "not json", "http://do-translate/");
        Translation translation = new Translation("text", "en-ru");

        extractor.extractTranslation(fakeResponse, translation);
    }

    @Test(expected = RuntimeException.class)
    public void extractTranslation_should_fail_when_yandex_response_has_not_text() throws Exception {
        String emptyJsonWithCode = TestData.createEmptyJsonWithCode(200);
        Response fakeResponse = TestData.createFakeResponse(200, emptyJsonWithCode, "http://do-translate/");
        Translation translation = new Translation("text", "en-ru");

        extractor.extractTranslation(fakeResponse, translation);
    }

    @Test
    public void extractSupportedLanguages_should_extract_supported_languages_from_correct_response() throws Exception {
        String langsJson = TestData.createLangsJson();
        Response fakeResponse = TestData.createFakeResponse(200, langsJson, "http://do-translate/?ui=en");
        Language[] expectedLanguages = TestData.getExtractedLangs();

        Language[] languages = extractor.extractLanguages(fakeResponse);

        assertArrayEquals(expectedLanguages, languages);
        assertEquals("Russian", languages[0].getTitle());
        assertEquals("English", languages[1].getTitle());
        assertEquals("French", languages[2].getTitle());
    }

    @Test(expected = RuntimeException.class)
    public void extractSupportedLanguages_should_fail_when_yandex_response_is_not_json() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "not json", "http://do-translate/");

        extractor.extractLanguages(fakeResponse);
    }

    @Test(expected = RuntimeException.class)
    public void extractSupportedLanguages_should_fail_when_yandex_response_json_is_empty() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "{}", "http://do-translate/");

        extractor.extractLanguages(fakeResponse);
    }
}