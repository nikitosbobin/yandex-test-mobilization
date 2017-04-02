package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.activity.MainActivity;
import com.nikit.bobin.wordstranslate.activity.SplashActivity;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.SettingsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslationFragment;
import com.nikit.bobin.wordstranslate.customviews.CircularCustomToggle;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;

import javax.inject.Singleton;

import dagger.Component;
// not need tests
// refactored
@Singleton
@Component(modules = {NetModule.class, ConcurrentModule.class,
        LoggingModule.class, TranslationModule.class, StorageModule.class})
public interface AppComponent {
    void injectSplashScreen(SplashActivity splashActivity);
    void injectsMainActivity(MainActivity mainActivity);
    void injectTranslationFragment(TranslationFragment fragment);
    void injectFavoriteTranslationsFragment(FavoriteTranslationsFragment fragment);
    void injectSettingsFragment(SettingsFragment fragment);
    void injectSelectorView(LanguageSelectorView languageSelectorView);
    void injectCircularCustomToggle(CircularCustomToggle circularCustomToggle);
}
