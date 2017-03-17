package com.nikit.bobin.wordstranslate;

import android.app.Application;

import com.nikit.bobin.wordstranslate.ioc.AppComponent;
import com.nikit.bobin.wordstranslate.ioc.DaggerAppComponent;

public class App extends Application {
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.create();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
