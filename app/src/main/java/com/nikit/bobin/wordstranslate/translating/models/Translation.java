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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        if (!direction.equals(that.direction)) return false;
        return translatedText.equals(that.translatedText);

    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + translatedText.hashCode();
        return result;
    }
}
