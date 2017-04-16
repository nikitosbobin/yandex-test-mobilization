package com.nikit.bobin.wordstranslate.storage;

import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.util.List;

//Implementation of ILanguagesDatabase. Uses Sugar ORM
public class LanguagesDatabase implements ILanguagesDatabase {
    @Override
    /*Returns language from database by target key with current ui language title.
    *   Returns null when ui is not suitable or languageKey not saved.
    */
    public Language getLanguage(String languageKey, Language ui) {
        if (languageKey != null && ui != null && isLanguagesSaved(ui)) {
            List<Language> languages = Language.find(Language.class, "KEY=?", languageKey);
            if (languages.size() > 0)
                return languages.get(0);
            return new Language(languageKey);
        }
        return null;
    }

    @Override
    public Language[] getLanguages(boolean orderDescending) {
        List<Language> languages =
                Language.listAll(
                        Language.class,
                        "TITLE " + (orderDescending ? "DESC" : "ASC"));
        return languages.toArray(new Language[languages.size()]);
    }

    @Override
    public boolean replaceLanguages(Language[] languages, Language ui) {
        if (languages == null
                || ui == null
                || (isLanguagesSaved(ui) && Language.count(Language.class) == languages.length))
            return false;
        if (Language.count(Language.class) > 0)
            Language.deleteAll(Language.class);
        try {
            for (Language language : languages) {
                if (language.equals(ui))
                    language = language.asUiLanguage();
                language.save();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isLanguagesSaved(Language ui) {
        if (ui == null)
            return false;
        long count = Language.count(
                Language.class,
                "KEY=? and IS_UI=1",
                new String[]{ui.getKey()});
        return count > 0;
    }
}
