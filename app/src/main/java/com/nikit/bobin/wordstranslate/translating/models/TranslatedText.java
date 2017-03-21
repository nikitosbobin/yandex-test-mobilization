package com.nikit.bobin.wordstranslate.translating.models;

public class TranslatedText {
    private boolean success;
    private String translatedText;
    private Translation translation;
    private int id;
    private boolean isFavorite;

    private TranslatedText() {
        id = -1;
    }

    public static TranslatedText success(String translatedtext, Translation translation) {
        TranslatedText result = new TranslatedText();
        result.success = true;
        result.translatedText = translatedtext;
        result.translation = translation;
        return result;
    }

    public static TranslatedText fail(Translation translation) {
        TranslatedText result = new TranslatedText();
        result.success = false;
        result.translation = translation;
        return result;
    }

    public static TranslatedText fromDatabase(int id, String translatedText, String originalText,
                                              String direction, boolean isFavorite) {
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
        if (translatedText != null ? !translatedText.equals(that.translatedText) : that.translatedText != null)
            return false;
        return translation.equals(that.translation);

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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean value) {
        isFavorite = value;
    }
}
