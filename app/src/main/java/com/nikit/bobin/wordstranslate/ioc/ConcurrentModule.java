package com.nikit.bobin.wordstranslate.ioc;

import org.jdeferred.DeferredManager;
import org.jdeferred.impl.DefaultDeferredManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConcurrentModule {
    @Provides
    @Singleton
    DeferredManager provideDeferredManager() {
        return new DefaultDeferredManager();
    }
}
