package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

//Abstraction of a component that provides methods for working with text translation
public interface ITranslator {
    TranslatedText translate(Translation translation);

    Language[] getLanguages();

    Language detectLanguage(String text);

    WordLookup getWordLookup(Translation translation);
}
