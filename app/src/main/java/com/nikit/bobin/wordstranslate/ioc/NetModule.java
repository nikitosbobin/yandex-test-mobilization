package com.nikit.bobin.wordstranslate.ioc;

import android.content.Context;

import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.HttpSender;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;

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

    @Provides
    @Singleton
    NetworkConnectionInfoProvider provideNetworkConnectionInfoProvider(Context context) {
        return new NetworkConnectionInfoProvider(context);
    }
}
