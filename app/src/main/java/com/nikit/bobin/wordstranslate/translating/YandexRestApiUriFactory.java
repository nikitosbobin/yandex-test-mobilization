package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class YandexRestApiUriFactory implements IYandexRestApiUriFactory {
    private final int maxCacheSize = 10000;
    private String translationKey;
    private String yandexTranslatorApiPrefix = "https://translate.yandex.net/api/v1.5/tr.json/";
    private String yandexDictionaryApiPrefix = "https://dictionary.yandex.net/api/v1/dicservice.json/";
    private String dictionaryKey;

    //todo: add caching
    public YandexRestApiUriFactory(String translationKey, String dictionaryKey) {
        Ensure.notNullOrEmpty(translationKey, "translationKey");
        Ensure.notNullOrEmpty(dictionaryKey, "dictionaryKey");

        this.translationKey = translationKey;
        this.dictionaryKey = dictionaryKey;
    }

    @Override
    public String getLangs(Language ui) {
        Ensure.notNull(ui, "ui");

        return String.format("%sgetLangs?key=%s&ui=%s", yandexTranslatorApiPrefix, translationKey, ui.getKey());
    }

    @Override
    public String translate(Direction direction, String text) {
        Ensure.notNull(direction, "direction");
        Ensure.notNullOrEmpty(text, "text");

        return String.format(
                "%stranslate?key=%s&lang=%s&text=%s",
                yandexTranslatorApiPrefix,
                translationKey,
                direction.toString(),
                screenSpecialSymbols(text));
    }

    @Override
    public String dictionaryLookup(Direction direction, String text) {
        Ensure.notNull(direction, "direction");
        Ensure.notNullOrEmpty(text, "text");

        return String.format(
                "%slookup?key=%s&lang=%s&text=%s",
                yandexDictionaryApiPrefix,
                dictionaryKey,
                direction.toString(),
                screenSpecialSymbols(text));
    }

    @Override
    public String detectLang(String text, Language... possibleLangs) {
        Ensure.notNullOrEmpty(text, "text");
        Ensure.notNull(possibleLangs, "possibleLangs");

        String[] langsKeys = new String[possibleLangs.length];
        for (int i = 0; i < possibleLangs.length; ++i)
            langsKeys[i] = possibleLangs[i].getKey();
        String hint = "";
        if (possibleLangs.length > 0)
            hint = "&hint=" + Strings.join(langsKeys, ",");

        return String.format(
                "%sdetect?key=%s&text=%s%s",
                yandexTranslatorApiPrefix,
                translationKey,
                screenSpecialSymbols(text),
                hint);
    }

    private static String screenSpecialSymbols(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return text;
        }
    }
}
