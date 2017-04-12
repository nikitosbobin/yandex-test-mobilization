package com.nikit.bobin.wordstranslate.storage;

//Functional interface, uses as translation database change event type
public interface OnTranslationDatabaseUpdateListener {
    void onTranslationDatabaseChange();
}
