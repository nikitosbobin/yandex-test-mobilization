package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.YandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.exceptions.NotSuccessfulResponseException;
import com.nikit.bobin.wordstranslate.translating.exceptions.ResponseHasNotTargetDataException;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
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
        extractor.extractSupportedLanguages(null);
    }

    @Test(expected = NullPointerException.class)
    public void extractSupportedDirections_should_fail_when_response_is_null() throws Exception {
        extractor.extractSupportedDirections(null);
    }

    @Test(expected = NullPointerException.class)
    public void extractTranslation_should_fail_when_response_is_null() throws Exception {
        extractor.extractTranslation(null);
    }

    @Test(expected = NotSuccessfulResponseException.class)
    public void extractSupportedLanguages_should_fail_when_response_is_not_success() throws Exception{
        extractor.extractSupportedLanguages(TestData.createFakeResponse(400, "", "http://fake"));
    }

    @Test(expected = NotSuccessfulResponseException.class)
    public void extractSupportedDirections_should_fail_when_response_is_not_success() throws Exception {
        extractor.extractSupportedDirections(TestData.createFakeResponse(400, "", "http://fake"));
    }

    @Test(expected = NotSuccessfulResponseException.class)
    public void extractTranslation_should_fail_when_response_is_not_success() throws Exception {
        extractor.extractTranslation(TestData.createFakeResponse(400, "", "http://fake"));
    }

    @Test
    public void extractTranslation_should_extract_translation_from_correct_response() throws Exception {
        String translationJson = TestData.createTranslationJson("ru-en", "dummy");
        Response fakeResponse = TestData.createFakeResponse(200, translationJson, "http://do-translate/");
        Translation expectedTranslation = new Translation("dummy", Direction.parse("ru-en"));

        Translation translation = extractor.extractTranslation(fakeResponse);

        assertEquals(expectedTranslation, translation);
    }

    @Test
    public void extractTranslation_should_return_null_translation_from_empty_response() throws Exception {
        String translationJson = TestData.createTranslationJson("ru-en", null);
        Response fakeResponse = TestData.createFakeResponse(200, translationJson, "http://do-translate/");

        Translation translation = extractor.extractTranslation(fakeResponse);

        assertEquals(null, translation);
    }

    @Test(expected = NotSuccessfulResponseException.class)
    public void extractTranslation_should_fail_when_yandex_response_code_in_not_successful() throws Exception {
        String translationJson = TestData.createFailJson(500);
        Response fakeResponse = TestData.createFakeResponse(200, translationJson, "http://do-translate/");

        extractor.extractTranslation(fakeResponse);
    }

    @Test(expected = ResponseHasNotTargetDataException.class)
    public void extractTranslation_should_fail_when_yandex_response_is_not_json() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "not json", "http://do-translate/");

        extractor.extractTranslation(fakeResponse);
    }

    @Test(expected = ResponseHasNotTargetDataException.class)
    public void extractTranslation_should_fail_when_yandex_response_has_not_text() throws Exception {
        String emptyJsonWithCode = TestData.createEmptyJsonWithCode(200);
        Response fakeResponse = TestData.createFakeResponse(200, emptyJsonWithCode, "http://do-translate/");

        extractor.extractTranslation(fakeResponse);
    }

    @Test
    public void extractSupportedLanguages_should_extract_supported_languages_from_correct_response() throws Exception {
        String langsJson = TestData.createLangsJson();
        Response fakeResponse = TestData.createFakeResponse(200, langsJson, "http://do-translate/?ui=en");
        Language[] expectedLanguages = TestData.getExtractedLangs();

        Language[] languages = extractor.extractSupportedLanguages(fakeResponse);

        assertArrayEquals(expectedLanguages, languages);
        assertEquals("Russian", languages[0].getTitle(new Language("en")));
        assertEquals("English", languages[1].getTitle(new Language("en")));
        assertEquals("French", languages[2].getTitle(new Language("en")));
    }

    @Test(expected = ResponseHasNotTargetDataException.class)
    public void extractSupportedLanguages_should_fail_when_yandex_response_is_not_json() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "not json", "http://do-translate/");

        extractor.extractSupportedLanguages(fakeResponse);
    }

    @Test(expected = ResponseHasNotTargetDataException.class)
    public void extractSupportedLanguages_should_fail_when_yandex_response_json_is_empty() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "{}", "http://do-translate/");

        extractor.extractSupportedLanguages(fakeResponse);
    }

    @Test
    public void extractSupportedDirections_should_extract_supported_languages_from_correct_response() throws Exception {
        String langsJson = TestData.createDirectionsJson();
        Response fakeResponse = TestData.createFakeResponse(200, langsJson, "http://do-translate/?ui=en");
        Direction[] extractedDirections = TestData.getExtractedDirections();

        Direction[] directions = extractor.extractSupportedDirections(fakeResponse);

        assertArrayEquals(extractedDirections, directions);
    }

    @Test(expected = ResponseHasNotTargetDataException.class)
    public void extractSupportedDirections_should_fail_when_yandex_response_is_not_json() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "not json", "http://do-translate/");

        extractor.extractSupportedDirections(fakeResponse);
    }

    @Test(expected = ResponseHasNotTargetDataException.class)
    public void extractSupportedDirections_should_fail_when_yandex_response_json_is_empty() throws Exception {
        Response fakeResponse = TestData.createFakeResponse(200, "{}", "http://do-translate/");

        extractor.extractSupportedDirections(fakeResponse);
    }
}