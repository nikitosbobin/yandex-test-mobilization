package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.Response;

public interface IHttpSender {
    Response sendRequest(@NonNull String url,
                         @NonNull HttpMethod method,
                         @Nullable RequestBody body) throws IOException;
}
