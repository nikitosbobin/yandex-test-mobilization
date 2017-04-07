package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.YandexTranslatorCache;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YandexTranslatorCache_Tests {
    private ITranslationsDatabase translationsDatabase;
    private ILanguagesDatabase languagesDatabase;
    private YandexTranslatorCache cache;

    @Before
    public void setUp() {
        translationsDatabase = mock(ITranslationsDatabase.class);
        languagesDatabase = mock(ILanguagesDatabase.class);

        when(translationsDatabase.getAllTranslations(false))
                .thenReturn(new TranslatedText[]{
                        TranslatedText.fullCustomInit(1, "hi", "привет", "ru-en", false),
                        TranslatedText.fullCustomInit(1, "bye", "пока", "ru-en", false)
                });

        cache = new YandexTranslatorCache(translationsDatabase, languagesDatabase, 10);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_translationsDatabase_null() {
        new YandexTranslatorCache(null, languagesDatabase, 10);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_languagesDatabase_null() {
        new YandexTranslatorCache(translationsDatabase, null, 10);
    }

    @Test
    public void hasTranslation_should_return_false_when_translation_null() {
        assertFalse(cache.hasTranslation(null));
    }

    @Test
    public void hasTranslation_should_correctly_detect_added_translation() {
        Translation translation = new Translation("привет", "ru-en");

        assertTrue(cache.hasTranslation(translation));
    }

    @Test
    public void hasTranslation_should_correctly_detect_not_exists_translation() {
        Translation translation = new Translation("qwerty", "en-ru");

        assertFalse(cache.hasTranslation(translation));
    }

    @Test
    public void hasLookup_should_return_false_when_translation_null() {
        assertFalse(cache.hasLookup(null));
    }

    @Test
    public void hasLookup_should_return_false_when_lookup_not_contains() {
        assertFalse(cache.hasLookup(new Translation("1q", "ru-ru")));
    }

    @Test
    public void hasLookup_should_return_true_when_lookup_added_via_addLookup() {
        Translation translation = new Translation("1q", "ru-ru");
        cache.addLookup(WordLookup.empty(translation));

        assertTrue(cache.hasLookup(translation));
    }

    @Test
    public void addLookup_should_not_add_null_lookup() {
        assertFalse(cache.addLookup(null));
    }

    @Test
    public void addLookup_should_not_add_already_exists_lookup() {
        WordLookup emptyLookup = WordLookup.empty(new Translation("1q", "ru-ru"));

        assertTrue(cache.addLookup(emptyLookup));
        assertFalse(cache.addLookup(emptyLookup));
    }

    @Test
    public void hasDetection_should_return_false_when_text_null() {
        assertFalse(cache.hasDetection(null));
    }

    @Test
    public void hasDetection_should_return_false_when_detection_not_contains() {
        assertFalse(cache.hasDetection("text"));
    }

    @Test
    public void hasDetection_should_return_false_when_detection_added_via_addDetection() {
        cache.addDetection("text", new Language("en"));

        assertTrue(cache.hasDetection("text"));
    }

    @Test
    public void addDetection_should_not_add_null_text_detection() {
        assertFalse(cache.addDetection(null, new Language("en")));
    }

    @Test
    public void addDetection_should_not_add_null_lang_detection() {
        assertFalse(cache.addDetection("text", null));
    }

    @Test
    public void addDetection_should_not_add_already_added_detection() {
        assertTrue(cache.addDetection("text", new Language("en")));
        assertFalse(cache.addDetection("text", new Language("en")));
    }

    @Test
    public void hasLanguages_should_return_false_when_ui_null() {
        assertFalse(cache.hasLanguages(null));
    }

    @Test
    public void hasLanguages_should_return_false_when_ui_not_in_database_and_not_added_via_addLanguages() {
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(false);

        assertFalse(cache.hasLanguages(ui));
    }

    @Test
    public void hasLanguages_should_return_true_when_ui_contains_in_database() {
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(true);

        assertTrue(cache.hasLanguages(ui));
    }

    @Test
    public void hasLanguages_should_return_true_when_ui_added_via_addLanguages() {
        Language ui = new Language("en");
        cache.addLanguages(ui, new Language[0]);

        assertTrue(cache.hasLanguages(ui));
    }

    @Test
    public void addLanguages_should_not_add_langs_when_ui_null() {
        assertFalse(cache.addLanguages(null, new Language[0]));
    }

    @Test
    public void addLanguages_should_not_add_langs_when_langs_null() {
        assertFalse(cache.addLanguages(new Language("en"), null));
    }

    @Test
    public void addLanguages_should_not_add_langs_twice() {
        assertTrue(cache.addLanguages(new Language("en"), new Language[0]));
        assertFalse(cache.addLanguages(new Language("en"), new Language[0]));
    }

    @Test
    public void getLanguages_should_return_null_when_ui_null() {
        assertNull(cache.getLanguages(null));
    }

    @Test
    public void getLanguages_should_return_null_when_ui_not_saved() {
        assertNull(cache.getLanguages(new Language("fr")));
    }

    @Test
    public void getLanguages_should_return_added_langs_via_addLanguages() {
        Language[] languages = new Language[0];
        Language ui = new Language("en");
        cache.addLanguages(ui, languages);

        assertSame(languages, cache.getLanguages(ui));
    }

    @Test
    public void getLanguages_should_return_stored_langs_in_database() {
        Language[] languages = new Language[0];
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(true);
        when(languagesDatabase.getLanguages(true)).thenReturn(languages);

        assertSame(languages, cache.getLanguages(ui));
    }

    @Test
    public void getTranslation_should_return_null_when_translation_null() {
        assertNull(cache.getTranslation(null));
    }

    @Test
    public void getTranslation_should_return_null_when_translation_not_cached() {
        assertNull(cache.getTranslation(new Translation("qwe", "en-ru")));
    }

    @Test
    public void getTranslation_should_return_translation_from_database() {
        TranslatedText expectedTranslation = TranslatedText.fullCustomInit(1, "hi", "привет", "ru-en", false);

        TranslatedText translation = cache.getTranslation(new Translation("привет", "ru-en"));

        assertEquals(expectedTranslation, translation);
    }

    @Test
    public void getTranslation_should_return_already_cached_translation() {
        TranslatedText translation =
                TranslatedText.success("qwerty", new Translation("fdsds", "en-fr"));
        cache.addTranslation(translation);

        TranslatedText actualTranslation = cache.getTranslation(new Translation("fdsds", "en-fr"));

        assertEquals(actualTranslation, translation);
    }

    @Test
    public void getWordLookup_should_return_null_when_translation_null() {
        assertNull(cache.getWordLookup(null));
    }

    @Test
    public void getWordLookup_should_return_null_when_translation_not_cached() {
        assertNull(cache.getWordLookup(new Translation("text", "en-fr")));
    }

    @Test
    public void getWordLookup_should_return_added_lookup_via_addLookup() {
        Translation translation = new Translation("text", "en-fr");
        WordLookup wordLookup = WordLookup.empty(translation);
        cache.addLookup(wordLookup);

        assertSame(wordLookup, cache.getWordLookup(translation));
    }

    @Test
    public void getDetection_should_return_null_when_text_null() {
        assertNull(cache.getDetection(null));
    }

    @Test
    public void getDetection_should_return_null_when_detection_not_cached() {
        assertNull(cache.getDetection("text"));
    }

    @Test
    public void getDetection_should_return_added_detection_via_addDetection() {
        String text = "text";
        Language detection = new Language("en");
        cache.addDetection(text, detection);

        assertSame(detection, cache.getDetection(text));
    }

    @Test
    public void should_clear_languages_cache_when_overflow() {
        cache = new YandexTranslatorCache(translationsDatabase, languagesDatabase, 2);

        cache.addLanguages(new Language("en"), new Language[0]);
        cache.addLanguages(new Language("fr"), new Language[0]);
        cache.addLanguages(new Language("ru"), new Language[0]);

        assertFalse(cache.hasLanguages(new Language("en")));
        assertFalse(cache.hasLanguages(new Language("fr")));
        assertTrue(cache.hasLanguages(new Language("ru")));
    }

    @Test
    public void should_clear_detections_cache_when_overflow() {
        cache = new YandexTranslatorCache(translationsDatabase, languagesDatabase, 2);

        cache.addDetection("text1", new Language("en"));
        cache.addDetection("text2", new Language("fr"));
        cache.addDetection("text3", new Language("ru"));

        assertFalse(cache.hasDetection("text1"));
        assertFalse(cache.hasDetection("text2"));
        assertTrue(cache.hasDetection("text3"));
    }

    @Test
    public void should_clear_lookups_cache_when_overflow() {
        cache = new YandexTranslatorCache(translationsDatabase, languagesDatabase, 2);
        Translation translation1 = new Translation("text", "en-fr");
        Translation translation2 = new Translation("text", "en-en");
        Translation translation3 = new Translation("text", "en-ru");

        cache.addLookup(WordLookup.empty(translation1));
        cache.addLookup(WordLookup.empty(translation2));
        cache.addLookup(WordLookup.empty(translation3));

        assertFalse(cache.hasLookup(translation1));
        assertFalse(cache.hasLookup(translation2));
        assertTrue(cache.hasLookup(translation3));
    }

    @Test
    public void should_clear_translations_cache_when_overflow() {
        cache = new YandexTranslatorCache(translationsDatabase, languagesDatabase, 2);

        Translation translation1 = new Translation("привет", "ru-en");
        Translation translation2 = new Translation("пока", "ru-en");
        Translation translation3 = new Translation("text", "en-ru");

        // first two translations contains in database
        cache.addTranslation(TranslatedText.success("translated", translation3));

        assertFalse(cache.hasTranslation(translation1));
        assertFalse(cache.hasTranslation(translation2));
        assertTrue(cache.hasTranslation(translation3));
    }
}
