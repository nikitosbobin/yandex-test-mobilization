package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.helpers.Ensure;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

@Table
//Model describes translation: translated text and and original translation
public class TranslatedText extends SugarRecord {
    private String translatedText;
    @Ignore
    private Translation translation;
    private String direction;
    private String originalText;
    private Long id;
    private boolean isFavorite;

    // Don't use this constructor. It only for Sugar ORM usage.
    @Deprecated
    public TranslatedText() {
        id = (long) -1;
    }

    public static TranslatedText success(String translatedText, Translation translation) {
        Ensure.notNullOrEmpty(translatedText, "translatedText");
        Ensure.notNull(translation, "translation");

        TranslatedText result = new TranslatedText();
        result.translatedText = translatedText;
        result.setTranslation(translation);
        return result;
    }

    public static TranslatedText fail(Translation translation) {
        Ensure.notNull(translation, "translation");

        TranslatedText result = new TranslatedText();
        result.setTranslation(translation);
        return result;
    }

    public static TranslatedText fullCustomInit(long id, String translatedText, String originalText,
                                                String direction, boolean isFavorite) {
        Ensure.notNullOrEmpty(translatedText, "translatedText");
        Ensure.notNullOrEmpty(originalText, "originalText");
        Ensure.notNullOrEmpty(direction, "direction");

        TranslatedText result = new TranslatedText();
        result.id = id;
        result.translatedText = translatedText;
        result.translation = new Translation(originalText, Direction.parseKeySerialized(direction));
        result.originalText = originalText;
        result.direction = direction;
        result.isFavorite = isFavorite;
        return result;
    }

    public boolean isSuccess() {
        return translatedText != null;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public Translation getTranslation() {
        if (translation == null)
            translation = new Translation(originalText, direction);
        return translation;
    }

    private void setTranslation(Translation translation) {
        this.translation = translation;
        originalText = translation.getOriginalText();
        direction = translation.getDirection().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslatedText that = (TranslatedText) o;
        if (translatedText != null
                ? !translatedText.equals(that.translatedText)
                : that.translatedText != null)
            return false;
        return getTranslation().equals(that.getTranslation());
    }

    @Override
    public int hashCode() {
        if (direction == null && originalText == null && translatedText == null)
            return super.hashCode();
        int result = translatedText != null ? translatedText.hashCode() : 0;
        result = 31 * result + getTranslation().hashCode();
        return result;
    }

    public Long getId() {
        return id;
    }

    public TranslatedText setFavorite(boolean value) {
        isFavorite = value;
        return this;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
