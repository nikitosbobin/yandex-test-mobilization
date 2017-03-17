package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.activity.TranslateActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetModule.class, ConcurrentModule.class, LoggingModule.class, TranslationModule.class})
public interface AppComponent {
    void injectsMainActivity(TranslateActivity mainActivity);
}
