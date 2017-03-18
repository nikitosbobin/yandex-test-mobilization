package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.activity.TranslateActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetModule.class, ConcurrentModule.class,
        LoggingModule.class, TranslationModule.class, StorageModule.class})
public interface AppComponent {
    void injectsMainActivity(TranslateActivity mainActivity);
    void injectApp(App app);
}
