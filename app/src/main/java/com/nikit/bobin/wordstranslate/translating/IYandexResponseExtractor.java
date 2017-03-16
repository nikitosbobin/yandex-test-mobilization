package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.exceptions.NotSuccessfulResponseException;
import com.nikit.bobin.wordstranslate.translating.exceptions.ResponseHasNotTargetDataException;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import java.io.IOException;

import okhttp3.Response;

public interface IYandexResponseExtractor {
    Language[] extractSupportedLanguages(Response response)
            throws ResponseHasNotTargetDataException, NotSuccessfulResponseException, IOException;

    Direction[] extractSupportedDirections(Response response)
            throws ResponseHasNotTargetDataException, NotSuccessfulResponseException, IOException;

    Translation extractTranslation(Response response)
            throws ResponseHasNotTargetDataException, NotSuccessfulResponseException, IOException;
}
