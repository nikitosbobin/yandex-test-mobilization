package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.jdeferred.Promise;
// not need tests
// refactored
public interface ITranslator {
    Promise<TranslatedText, Throwable, Void> translateAsync(Translation translation);
    Promise<Language[], Throwable, Void> getLanguagesAsync();
    Promise<Language, Throwable, Void> detectLanguageAsync(String text);
    Promise<WordLookup, Throwable, Void> getWordLookupAsync(Translation translation);
}
