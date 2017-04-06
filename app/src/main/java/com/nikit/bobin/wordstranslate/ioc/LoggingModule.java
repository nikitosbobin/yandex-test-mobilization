package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.logging.SimpleLogger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LoggingModule {
    @Provides
    @Singleton
    ILog provideLogger() {
        return new SimpleLogger();
    }
}
