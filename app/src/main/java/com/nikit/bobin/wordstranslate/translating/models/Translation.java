package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

//todo: to struct
public class Translation {
    private Direction direction;
    private String originalText;

    //test: originalText
    public Translation(String originalText, Direction direction) {
        Ensure.notNullOrEmpty(originalText, "originalText");
        Ensure.notNull(direction, "direction");

        this.direction = direction;
        this.originalText = originalText;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getOriginalText() {
        return originalText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        if (!direction.equals(that.direction)) return false;
        return originalText.equals(that.originalText);
    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + originalText.hashCode();
        return result;
    }
}
