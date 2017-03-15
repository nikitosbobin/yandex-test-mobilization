package com.nikit.bobin.wordstranslate.translating;

import org.jdeferred.Promise;

import java.util.HashMap;

public interface ITranslator {
    Promise<String, Throwable, Void> translateAsync(String word, String direction);
    Promise<HashMap<String, String>, Throwable, Void> getSupportedLangsAsync(final String uiLang);
    Promise<String[], Throwable, Void> getSupportedDirectionsAsync();

}
