package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.activity.MainActivity;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslationFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetModule.class, ConcurrentModule.class,
        LoggingModule.class, TranslationModule.class, StorageModule.class})
public interface AppComponent {
    void injectsMainActivity(MainActivity mainActivity);
    void injectTranslationFragment(TranslationFragment fragment);
    void injectFavoriteTranslationsFragment(FavoriteTranslationsFragment fragment);
    void injectApp(App app);
}
