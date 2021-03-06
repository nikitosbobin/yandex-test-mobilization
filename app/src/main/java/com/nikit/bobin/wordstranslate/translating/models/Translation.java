package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.helpers.Ensure;

//Model describes future translation: original text and direction of translation
public class Translation {
    private Direction direction;
    private String originalText;
    private int wordCount;

    public Translation(String originalText, Direction direction) {
        Ensure.notNullOrEmpty(originalText, "originalText");
        Ensure.notNull(direction, "direction");

        this.direction = direction;
        this.originalText = originalText;
        wordCount = originalText.split(" ").length;
    }

    public Translation(String originalText, String direction) {
        Ensure.notNullOrEmpty(originalText, "originalText");
        Ensure.notNullOrEmpty(direction, "direction");

        this.direction = Direction.parseKeySerialized(direction);
        this.originalText = originalText;
        wordCount = originalText.split(" ").length;
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

    public int getWordCount() {
        return wordCount;
    }
}
