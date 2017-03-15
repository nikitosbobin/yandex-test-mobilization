package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nikit.bobin.wordstranslate.core.Ensure;

import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;

import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpSender implements IHttpSender {
    private DeferredManager deferredManager;
    private OkHttpClient client;

    public HttpSender(DeferredManager deferredManager) {
        Ensure.notNull(deferredManager, "deferredManager");

        this.deferredManager = deferredManager;
        client = new OkHttpClient();
    }

    @Override
    public Promise<Response, Throwable, Void> sendRequest(
            @NonNull String url,
            @NonNull HttpMethod method,
            @Nullable byte[] body,
            @Nullable String mediaType) {
        Ensure.notNullOrEmpty(url, "url");
        Ensure.urlIsPresent(url, "url");

        return createPromise(url, method, body, mediaType);
    }

    @Override
    public Promise<Response, Throwable, Void> sendRequest(
            @NonNull String url,
            @NonNull HttpMethod method) {
        return sendRequest(url, method, null, null);
    }

    @Override
    public Promise<Response, Throwable, Void> sendRequest(
            @NonNull String url,
            @NonNull HttpMethod method,
            @Nullable String body) {
        if (body == null)
            return sendRequest(url, method, null, null);
        return sendRequest(url, method, body.getBytes(Charset.forName("utf-8")), "application/x-www-form-urlencoded");
    }

    private Promise<Response, Throwable, Void> createPromise(
            final String url,
            final HttpMethod method,
            final byte[] body,
            final String mediaType) {
        return deferredManager.when(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                Call call = createCall(url, method, body, mediaType);
                Response response = call.execute();
                return response;
            }
        });
    }

    private Call createCall(String url, HttpMethod method, byte[] body, String mediaType) {
        RequestBody requestBody = null;
        if (body != null && mediaType != null) {
            requestBody = RequestBody.create(MediaType.parse(mediaType), body);
        }
        Request request = new Request.Builder()
                .method(method.name(), requestBody)
                .url(url)
                .build();

        return client.newCall(request);
    }
}
