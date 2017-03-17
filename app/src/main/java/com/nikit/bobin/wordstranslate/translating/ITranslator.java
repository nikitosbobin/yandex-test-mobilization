package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.Promise;

public interface ITranslator {
    Promise<TranslatedText, Throwable, Void> translateAsync(Translation translation);
    Promise<Language[], Throwable, Void> getLanguagesAsync();
}
