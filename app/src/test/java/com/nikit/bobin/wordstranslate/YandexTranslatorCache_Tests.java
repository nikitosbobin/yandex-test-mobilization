package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.YandexTranslatorCache;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneFilter;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class YandexTranslatorCache_Tests {
    private ITranslationsDatabase translationsDatabase;
    private ILanguagesDatabase languagesDatabase;
    private  YandexTranslatorCache cache;

    @Before
    public void setUp() {
        translationsDatabase = mock(ITranslationsDatabase.class);
        languagesDatabase = mock(ILanguagesDatabase.class);

        when(translationsDatabase.isConnected()).thenReturn(true);

        when(translationsDatabase.getAllTranslations(false))
                .thenReturn(new TranslatedText[] {
                        TranslatedText.fromDatabase(1, "hi", "привет", "ru-en", false),
                        TranslatedText.fromDatabase(1, "bye", "пока", "ru-en", false)
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
    public void hasTranslation_should_correctly_detect_exists_translation() {
        Translation translation = new Translation("привет", "ru-en");

        assertTrue(cache.hasTranslation(translation));
    }

    @Test
    public void hasTranslation_should_correctly_detect_not_exists_translation() {
        Translation translation = new Translation("qwerty", "en-ru");

        assertFalse(cache.hasTranslation(translation));
    }

    @Test
    public void langsCached_should_correctly_delegate_logic_to_database() {
        Language ui = new Language("ru");

        cache.langsCached(ui);

        verify(languagesDatabase).isLanguagesSaved(ui);
    }

    @Test
    public void addLanguages_should_save_supported_languages_to_database_when_not_saved_yet() {
        Language[] languages = new Language[] {
                new Language("ru", "Russian"),
                new Language("en", "English")
        };
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(false);

        cache.addLanguages(languages, ui);

        verify(languagesDatabase).replaceLanguages(languages, ui);
    }
    @Test
    public void addLanguages_should_not_save_supported_languages_to_database_when_already_saved() {
        Language[] languages = new Language[] {
                new Language("ru", "Russian"),
                new Language("en", "English")
        };
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(true);

        cache.addLanguages(languages, ui);

        verify(languagesDatabase, never()).replaceLanguages(languages, ui);
    }

    @Test
    public void getLanguages_should_return_empty_result_when_languages_not_saved_yet() {
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(false);

        Language[] languages = cache.getLanguages(ui);

        assertEquals(0, languages.length);
    }

    @Test
    public void getLanguages_should_return_empty_result_when_languages_only_saved_in_database() {
        Language ui = new Language("en");
        when(languagesDatabase.isLanguagesSaved(ui)).thenReturn(true);

        cache.getLanguages(ui);

        verify(languagesDatabase).getLanguages(false);
    }

    @Test
    public void getLanguages_should_return_empty_result_when_languages_has_been_added() {
        Language[] languages = new Language[] {
                new Language("ru", "Russian"),
                new Language("en", "English")
        };
        Language ui = new Language("en");
        cache.addLanguages(ui, languages);

        Language[] actualLanguages = cache.getLanguages(ui);

        verify(languagesDatabase, never()).getLanguages(false);
        assertSame(languages, actualLanguages);
    }

    @Test
    public void getTranslation_should_return_null_when_translation_not_cached() {
        TranslatedText translation = cache.getTranslation(new Translation("qwe", "en-ru"));

        assertNull(translation);
    }

    @Test
    public void getTranslation_should_return_translation_from_database() {
        TranslatedText expectedTranslation = TranslatedText.fromDatabase(1, "hi", "привет", "ru-en", false);

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
}
