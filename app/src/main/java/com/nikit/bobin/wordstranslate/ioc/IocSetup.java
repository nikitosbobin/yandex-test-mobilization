package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;

public final class IocSetup {
    private static AppComponent component;

    public static void setup(Context context) {
        component = DaggerAppComponent.builder()
                .translationModule(new TranslationModule(context))
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
