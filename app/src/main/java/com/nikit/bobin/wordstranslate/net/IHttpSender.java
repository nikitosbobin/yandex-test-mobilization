package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jdeferred.Promise;

import okhttp3.Response;

public interface IHttpSender {
    Promise<Response, Throwable, Void> sendRequest(@NonNull String url,
                                                   @NonNull HttpMethod method);

    Promise<Response, Throwable, Void> sendRequest(@NonNull String url,
                                                   @NonNull HttpMethod method,
                                                   @Nullable byte[] body,
                                                   @Nullable String mediaType);

    Promise<Response, Throwable, Void> sendRequest(@NonNull String url,
                                                   @NonNull HttpMethod method,
                                                   @Nullable String body);
}
