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
}
