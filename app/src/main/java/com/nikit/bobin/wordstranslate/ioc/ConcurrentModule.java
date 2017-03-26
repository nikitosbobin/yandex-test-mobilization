package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;
import android.os.Handler;

import org.jdeferred.DeferredManager;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.impl.DefaultDeferredManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConcurrentModule {
    @Provides
    @Singleton
    DeferredManager provideDeferredManager() {
        return new AndroidDeferredManager();
    }

    @Provides
    @Singleton
    Handler provideUiHandler(Context context) {
        return new Handler(context.getMainLooper());
    }
}
