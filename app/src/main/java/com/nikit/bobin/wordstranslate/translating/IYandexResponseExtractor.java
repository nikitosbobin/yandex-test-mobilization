package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import okhttp3.Response;

/*The abstraction of the component that extracts the translation
*    results from the Yandex api response
*/
public interface IYandexResponseExtractor {
    Language[] extractLanguages(Response response);

    TranslatedText extractTranslation(Response response, Translation translation);

    Language extractDetectedLanguage(Response response);

    WordLookup extractWordLookup(Response response, Translation translation);
}
