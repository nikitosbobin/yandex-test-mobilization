package com.nikit.bobin.wordstranslate.ioc;

import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.HttpSender;
import com.nikit.bobin.wordstranslate.net.IHttpSender;

import org.jdeferred.DeferredManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class NetModule {
    @Provides
    @Singleton
    OkHttpClient provideHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    IHttpSender provideHttpSender(DeferredManager deferredManager, OkHttpClient httpClient, ILog log) {
        return new HttpSender(httpClient, deferredManager, log);
    }
}
