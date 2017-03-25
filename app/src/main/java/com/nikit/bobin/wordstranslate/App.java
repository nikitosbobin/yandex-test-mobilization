package com.nikit.bobin.wordstranslate;

import android.app.Application;

import com.nikit.bobin.wordstranslate.ioc.AppComponent;
import com.nikit.bobin.wordstranslate.ioc.DaggerAppComponent;
import com.nikit.bobin.wordstranslate.ioc.TranslationModule;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;

import javax.inject.Inject;

// refactored
public class App extends Application {
    private static AppComponent component;
    @Inject
    ITranslationsDatabase translationsDatabase;
    @Inject
    ILanguagesDatabase languagesDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        // Configuring DI container
        component = DaggerAppComponent.builder()
                .translationModule(new TranslationModule(this))
                .build();
        // Dependency injection
        component.injectApp(this);
        // Connecting to databases
        translationsDatabase.connect(this);
        languagesDatabase.connect(this);
    }

    public static AppComponent getComponent() {
        return component;
    }

}
