package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

public interface IYandexRestApiUriFactory {
    String getLangs();

    String getLangs(Language ui);

    String translate(Direction direction, String text);

    String dictionaryLookup(Direction direction, String text);

    String detectLang(String text, Language... possibleLangs);
}
