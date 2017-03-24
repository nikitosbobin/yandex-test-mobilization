package com.nikit.bobin.wordstranslate;

import android.app.Application;

import com.nikit.bobin.wordstranslate.ioc.AppComponent;
import com.nikit.bobin.wordstranslate.ioc.DaggerAppComponent;
import com.nikit.bobin.wordstranslate.ioc.TranslationModule;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;

import java.util.Date;

import javax.inject.Inject;

public class App extends Application {
    private static AppComponent component;
    @Inject
    ITranslationsDatabase translationsDatabase;
    @Inject
    ILanguagesDatabase languagesDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .translationModule(new TranslationModule(this))
                .build();
        component.injectApp(this);
        translationsDatabase.connect(this);
        languagesDatabase.connect(this);
    }

    public static AppComponent getComponent() {
        return component;
    }

}
