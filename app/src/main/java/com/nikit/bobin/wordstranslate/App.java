package com.nikit.bobin.wordstranslate;

import android.app.Application;

import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.ioc.AppComponent;
import com.nikit.bobin.wordstranslate.ioc.DaggerAppComponent;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

public class App extends Application {
    private static AppComponent component;
    @Inject ITranslator translator;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.create();
        component.injectApp(this);
        initSupportedLanguages();
    }

    public static AppComponent getComponent() {
        return component;
    }

    private void initSupportedLanguages() {
        translator
                .getLanguagesAsync()
                .then(new DoneCallback<Language[]>() {
                    public void onDone(Language[] result) {
                        LanguageSelectorView.setSupportedLanguages(result);
                    }
                });
    }
}
