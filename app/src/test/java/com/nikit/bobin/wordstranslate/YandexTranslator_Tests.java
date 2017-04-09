package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.net.HttpMethod;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.translating.IYandexResponseExtractor;
import com.nikit.bobin.wordstranslate.translating.IYandexRestApiUriFactory;
import com.nikit.bobin.wordstranslate.translating.YandexTranslator;
import com.nikit.bobin.wordstranslate.translating.YandexTranslatorCache;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class YandexTranslator_Tests {
    private IHttpSender httpSender;
    private Language ui;
    private IYandexRestApiUriFactory uriFactory;
    private IYandexResponseExtractor responseExtractor;
    private ILanguagesDatabase languagesDatabase;
    private YandexTranslatorCache cache;
    private YandexTranslator translator;
    private Language[] languages;

    @Before
    public void setUp() {
        httpSender = mock(IHttpSender.class);
        ui = new Language("en");
        uriFactory = mock(IYandexRestApiUriFactory.class);
        responseExtractor = mock(IYandexResponseExtractor.class);
        cache = mock(YandexTranslatorCache.class);
        languagesDatabase = mock(ILanguagesDatabase.class);

        languages = new Language[]{
                new Language("ru", "Russuan"),
                new Language("en", "English")
        };
        when(responseExtractor.extractLanguages(any(Response.class))).thenReturn(languages);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_httpSender_null() {
        new YandexTranslator(
                null,
                ui,
                uriFactory,
                responseExtractor,
                languagesDatabase,
                cache);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_ui_null() {
        new YandexTranslator(
                httpSender,
                null,
                uriFactory,
                responseExtractor,
                languagesDatabase,
                cache);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_uriFactory_null() {
        new YandexTranslator(
                httpSender,
                ui,
                null,
                responseExtractor,
                languagesDatabase,
                cache);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_responseExtractor_null() {
        new YandexTranslator(
                httpSender,
                ui,
                uriFactory,
                null,
                languagesDatabase,
                cache);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_languagesDatabase_null() {
        new YandexTranslator(
                httpSender,
                ui,
                uriFactory,
                responseExtractor,
                null,
                cache);
    }

    @Test(expected = NullPointerException.class)
    public void translate_should_fail_when_translation_null() {
        translator = createTranslator(true);

        translator.translate(null);
    }

    @Test
    public void translate_should_return_cached_translation_if_cache_required() {
        translator = createTranslator(true);
        Translation translation = mock(Translation.class);
        TranslatedText expectedTranslation = mock(TranslatedText.class);
        when(cache.hasTranslation(translation)).thenReturn(true);
        when(cache.getTranslation(translation)).thenReturn(expectedTranslation);

        TranslatedText actualTranslation = translator.translate(translation);

        verify(cache).getTranslation(translation);
        assertEquals(expectedTranslation, actualTranslation);
    }

    @Test
    public void translateAsync_should_return_failed_translation_when_any_error_occurs()
            throws IOException {
        translator = createTranslator(false);
        Translation translation = new Translation("rrr", "en-ru");
        TranslatedText expectedTranslation = TranslatedText.fail(translation);
        when(uriFactory.translate(translation.getDirection(), translation.getOriginalText()))
                .thenReturn("http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null))
                .thenThrow(new RuntimeException());

        TranslatedText actualTranslation = translator.translate(translation);

        assertEquals(expectedTranslation, actualTranslation);
    }

    @Test
    public void translateAsync_should_send_translation_request_if_cache_not_required()
            throws IOException, InterruptedException {
        translator = createTranslator(false);
        Translation translation = new Translation("text", "en-ru");
        when(uriFactory.translate(translation.getDirection(), translation.getOriginalText()))
                .thenReturn("http://path");
        Response response = TestData.createFakeResponse(200, "response", "http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenReturn(response);

        translator.translate(translation);

        verify(responseExtractor).extractTranslation(response, translation);
    }

    @Test
    public void translateAsync_should_send_translation_request_and_cache_if_required()
            throws IOException, InterruptedException {
        translator = createTranslator(true);
        Translation translation = new Translation("text", "en-ru");
        TranslatedText expectedTranslation = mock(TranslatedText.class);
        when(uriFactory.translate(translation.getDirection(), translation.getOriginalText()))
                .thenReturn("http://path");
        Response response = TestData.createFakeResponse(200, "response", "http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenReturn(response);
        when(responseExtractor.extractTranslation(response, translation)).thenReturn(expectedTranslation);

        translator.translate(translation);

        verify(responseExtractor).extractTranslation(response, translation);
        verify(cache).addTranslation(expectedTranslation);
    }

    @Test
    public void getLanguagesAsync_should_return_cached_languages_if_cache_required() {
        translator = createTranslator(true);
        when(cache.hasLanguages(ui)).thenReturn(true);
        when(cache.getLanguages(ui)).thenReturn(languages);

        Language[] actualLanguages = translator.getLanguages();

        verify(cache).getLanguages(ui);
        assertEquals(languages, actualLanguages);
    }

    @Test
    public void getLanguagesAsync_should_send_request_if_cache_not_required() throws Exception {
        translator = createTranslator(false);
        when(cache.hasLanguages(ui)).thenReturn(true);
        when(cache.getLanguages(ui)).thenReturn(languages);
        Response response = TestData.createFakeResponse(200, "response", "http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenReturn(response);
        when(uriFactory.getLangs(ui)).thenReturn("http://path");

        translator.getLanguages();

        verify(responseExtractor).extractLanguages(response);
    }

    @Test
    public void getLanguagesAsync_should_send_request_and_cache_if_required() throws Exception {
        translator = createTranslator(true);
        Response response = TestData.createFakeResponse(200, "response", "http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenReturn(response);
        when(uriFactory.getLangs(ui)).thenReturn("http://path");

        translator.getLanguages();

        verify(responseExtractor).extractLanguages(response);
        verify(cache).addLanguages(ui, languages);
    }

    @Test(expected = NullPointerException.class)
    public void detectLanguageAsync_should_fail_when_text_null() {
        translator.detectLanguage(null);
    }

    @Test
    public void detectLanguageAsync_should_return_cached_detection_if_cache_required() {
        translator = createTranslator(true);
        Language expectedDetection = mock(Language.class);
        when(cache.hasDetection("text")).thenReturn(true);
        when(cache.getDetection("text")).thenReturn(expectedDetection);

        Language actualDetection = translator.detectLanguage("text");

        verify(cache).getDetection("text");
        assertEquals(expectedDetection, actualDetection);
    }

    @Test
    public void detectLanguageAsync_should_send_request_and_cache_if_required() throws Exception {
        translator = createTranslator(true);
        Response response = TestData.createFakeResponse(200, "response", "http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenReturn(response);
        when(uriFactory.detectLang("text", new Language("ru"), new Language("en"))).thenReturn("http://path");
        when(responseExtractor.extractDetectedLanguage(response)).thenReturn(new Language("fr"));
        when(languagesDatabase.getLanguage("fr", ui)).thenReturn(new Language("fr"));

        translator.detectLanguage("text");

        verify(responseExtractor).extractDetectedLanguage(response);
        verify(cache).addDetection("text", new Language("fr"));
    }

    @Test
    public void detectLanguageAsync_should_return_null_when_any_error_occurs() throws Exception {
        translator = createTranslator(false);
        when(uriFactory.detectLang("text", new Language("ru"), new Language("en"))).thenReturn("http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenThrow(new RuntimeException());

        Language detection = translator.detectLanguage("text");

        assertNull(detection);
    }

    @Test(expected = NullPointerException.class)
    public void getWordLookupAsync_should_fail_when_translation_null() {
        translator.getWordLookup(null);
    }

    @Test
    public void getWordLookupAsync_should_return_empty_lookup_when_translation_has_more_than_one_word() {
        Translation translation = new Translation("word word", "en-ru");
        translator = createTranslator(false);

        WordLookup actualLookup = translator.getWordLookup(translation);

        assertTrue(actualLookup.isEmpty());
    }

    @Test
    public void getWordLookupAsync_should_return_cached_lookup_if_cache_required() {
        Translation translation = new Translation("word", "en-ru");
        translator = createTranslator(true);
        WordLookup lookup = mock(WordLookup.class);
        when(cache.hasLookup(translation)).thenReturn(true);
        when(cache.getWordLookup(translation)).thenReturn(lookup);

        WordLookup actualLookup = translator.getWordLookup(translation);

        assertSame(lookup, actualLookup);
    }

    @Test
    public void getWordLookupAsync_should_return_correct_lookup_and_cache_if_required() throws IOException {
        WordLookup wordLookup = mock(WordLookup.class);
        Translation translation = new Translation("word", "en-ru");
        translator = createTranslator(true);
        when(uriFactory.dictionaryLookup(translation.getDirection(), translation.getOriginalText()))
                .thenReturn("http://path");
        Response response = TestData.createFakeResponse(200, "response", "http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenReturn(response);
        when(responseExtractor.extractWordLookup(response, translation)).thenReturn(wordLookup);

        WordLookup actualLookup = translator.getWordLookup(translation);

        assertSame(wordLookup, actualLookup);
        verify(cache).addLookup(wordLookup);
    }

    @Test
    public void getWordLookupAsync_should_return_empty_lookup_when_any_error_occurs() throws IOException {
        Translation translation = new Translation("word", "en-ru");
        translator = createTranslator(false);
        when(uriFactory.dictionaryLookup(translation.getDirection(), translation.getOriginalText()))
                .thenReturn("http://path");
        when(httpSender.sendRequest("http://path", HttpMethod.GET, null)).thenThrow(new RuntimeException());

        WordLookup actualLookup = translator.getWordLookup(translation);

        assertEquals(WordLookup.empty(translation), actualLookup);
    }

    private <T> T extractPromiseResult(Promise<T, Throwable, Void> promise) {
        final Object[] extractedResult = {null};
        try {
            promise
                    .then(new DoneCallback<T>() {
                        public void onDone(T result) {
                            extractedResult[0] = result;
                        }
                    })
                    .waitSafely();
        } catch (InterruptedException e) {
            return null;
        }
        return (T) extractedResult[0];
    }

    private YandexTranslator createTranslator(boolean withCache) {
        return new YandexTranslator(
                httpSender,
                ui,
                uriFactory,
                responseExtractor,
                languagesDatabase,
                withCache ? cache : null);
    }
}
