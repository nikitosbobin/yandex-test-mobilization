package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

public class YandexRestApiUriFactory implements IYandexRestApiUriFactory {
    private String appKey;
    private String yandexApiPrefix = "https://translate.yandex.net/api/v1.5/tr.json/";
    //todo: add caching
    public YandexRestApiUriFactory(String appKey) {
        Ensure.notNullOrEmpty(appKey, "appKey");

        this.appKey = appKey;
    }

    public YandexRestApiUriFactory() {
        this("trnsl.1.1.20170315T155530Z.982270abc72ef811.6b65d3680beb5b85a2f7ee473c7033c589c743a2");
    }

    @Override
    public String getLangs() {
        return String.format("%sgetLangs?key=%s", yandexApiPrefix, appKey);
    }

    @Override
    public String getLangs(Language ui) {
        Ensure.notNull(ui, "ui");

        return String.format("%sgetLangs?key=%s&ui=%s", yandexApiPrefix, appKey, ui.getKey());
    }

    @Override
    public String translate(Direction direction) {
        Ensure.notNull(direction, "direction");

        return String.format("%stranslate?key=%s&lang=%s", yandexApiPrefix, appKey, direction.toString());
    }
}
