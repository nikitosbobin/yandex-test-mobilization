package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;
// refactored
// tested
public class TranslatedText {
    private boolean success;
    private String translatedText;
    private Translation translation;
    private int id;
    private boolean isFavorite;

    private TranslatedText() {
        id = -1;
    }

    public static TranslatedText success(String translatedText, Translation translation) {
        Ensure.notNullOrEmpty(translatedText, "translatedText");
        Ensure.notNull(translation, "translation");

        TranslatedText result = new TranslatedText();
        result.success = true;
        result.translatedText = translatedText;
        result.translation = translation;
        return result;
    }

    public static TranslatedText fail(Translation translation) {
        Ensure.notNull(translation, "translation");

        TranslatedText result = new TranslatedText();
        result.success = false;
        result.translation = translation;
        return result;
    }

    public static TranslatedText fromDatabase(int id, String translatedText, String originalText,
                                              String direction, boolean isFavorite) {
        Ensure.notNullOrEmpty(translatedText, "translatedText");
        Ensure.notNullOrEmpty(originalText, "originalText");
        Ensure.notNullOrEmpty(direction, "direction");

        TranslatedText result = new TranslatedText();
        result.success = true;
        result.id = id;
        result.translatedText = translatedText;
        result.translation = new Translation(originalText, Direction.parse(direction));
        result.isFavorite = isFavorite;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public Translation getTranslation() {
        return translation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslatedText that = (TranslatedText) o;
        if (success != that.success) return false;
        return translatedText != null
                ? translatedText.equals(that.translatedText)
                : that.translatedText == null && translation.equals(that.translation);
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (translatedText != null ? translatedText.hashCode() : 0);
        result = 31 * result + translation.hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public TranslatedText changeFavoriteState(boolean value) {
        return fromDatabase(
                getId(),
                translatedText,
                getTranslation().getOriginalText(),
                getTranslation().getDirection().toString(),
                value);
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
