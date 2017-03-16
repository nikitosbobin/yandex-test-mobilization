package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import okhttp3.Response;

public interface IYandexResponseExtractor {
    Language[] extractSupportedLanguages(Response response);
    Direction[] extractSupportedDirections(Response response);
    Translation extractTranslation(Response response);
}
