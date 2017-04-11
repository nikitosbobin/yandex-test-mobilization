package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

//The abstraction of the component that creates a url to execute the query to yandex api
public interface IYandexRestApiUriFactory {
    String getLangs(Language ui);

    String translate(Direction direction, String text);

    String dictionaryLookup(Direction direction, String text);

    String detectLang(String text, Language... possibleLangs);
}
