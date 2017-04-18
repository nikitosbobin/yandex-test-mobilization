package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.activity.MainActivity;
import com.nikit.bobin.wordstranslate.activity.SplashActivity;
import com.nikit.bobin.wordstranslate.activity.fragments.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.fragments.SettingsFragment;
import com.nikit.bobin.wordstranslate.activity.fragments.TranslationFragment;
import com.nikit.bobin.wordstranslate.customviews.FloatingActionToggle;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        NetModule.class,
        ConcurrentModule.class,
        LoggingModule.class,
        TranslationModule.class,
        StorageModule.class})
public interface AppComponent {
    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);

    void inject(TranslationFragment fragment);

    void inject(FavoriteTranslationsFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(LanguageSelectorView languageSelectorView);

    void inject(FloatingActionToggle circularCustomToggle);
}
