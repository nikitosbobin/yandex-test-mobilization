package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

//todo: to struct
public class Translation {
    private Direction direction;
    private String translatedText;

    public Translation(String translatedText, Direction direction) {
        Ensure.notNullOrEmpty(translatedText, "translatedText");
        Ensure.notNull(direction, "direction");

        this.translatedText = translatedText;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getTranslatedText() {
        return translatedText;
    }
}
