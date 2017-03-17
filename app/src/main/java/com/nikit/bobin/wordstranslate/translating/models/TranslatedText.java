package com.nikit.bobin.wordstranslate.translating.models;

public class TranslatedText {
    private boolean success;
    private String translatedText;
    private Translation translation;

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
}
