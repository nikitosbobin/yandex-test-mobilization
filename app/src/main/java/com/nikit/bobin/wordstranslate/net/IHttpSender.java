package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jdeferred.Promise;

import okhttp3.RequestBody;
import okhttp3.Response;

public interface IHttpSender {
    Promise<Response, Throwable, Void> sendRequestAsync(@NonNull String url,
                                                        @NonNull HttpMethod method,
                                                        @Nullable RequestBody body);
}
