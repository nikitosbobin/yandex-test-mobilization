package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

public class YandexRestApiUriFactory implements IYandexRestApiUriFactory {
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

    public YandexRestApiUriFactory() {
        this(
                "trnsl.1.1.20170315T155530Z.982270abc72ef811.6b65d3680beb5b85a2f7ee473c7033c589c743a2",
                "dict.1.1.20170317T125256Z.5b1c51624e355923.679ec4c6fe21aca55d9e69779966b0b53d8147cf"
        );
    }

    @Override
    public String getLangs() {
        return String.format("%sgetLangs?key=%s", yandexTranslatorApiPrefix, translationKey);
    }

    @Override
    public String getLangs(Language ui) {
        Ensure.notNull(ui, "ui");

        return String.format("%sgetLangs?key=%s&ui=%s", yandexTranslatorApiPrefix, translationKey, ui.getKey());
    }

    @Override
    public String translate(Direction direction) {
        Ensure.notNull(direction, "direction");

        return String.format("%stranslate?key=%s&lang=%s", yandexTranslatorApiPrefix, translationKey, direction.toString());
    }

    @Override
    public String dictionaryLookup(Direction direction) {
        Ensure.notNull(direction, "direction");

        return String.format("%slookup?key=%s&lang=%s", yandexDictionaryApiPrefix, dictionaryKey, direction.toString());
    }
}
