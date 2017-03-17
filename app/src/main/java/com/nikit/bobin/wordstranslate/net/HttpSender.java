package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.logging.ILog;

import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;

import java.io.IOException;
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
    private final String defaultMediaType = "application/x-www-form-urlencoded";
    private ILog log;

    public HttpSender(OkHttpClient httpClient, DeferredManager deferredManager, ILog log) {
        Ensure.notNull(httpClient, "httpClient");
        Ensure.notNull(deferredManager, "deferredManager");
        Ensure.notNull(log, "log");

        this.deferredManager = deferredManager;
        client = httpClient;
        this.log = log;
    }

    @Override
    public Promise<Response, Throwable, Void> sendRequestAsync(
            @NonNull String url,
            @NonNull HttpMethod method,
            @Nullable byte[] body,
            @Nullable String mediaType) {
        Ensure.notNullOrEmpty(url, "url");
        Ensure.urlIsPresent(url, "url");
        if (body == null)
            return createPromise(url, method, new byte[0], defaultMediaType);
        return createPromise(url, method, body, mediaType);
    }
//todo: refactor
    @Override
    public Promise<Response, Throwable, Void> sendRequestAsync(
            @NonNull String url,
            @NonNull HttpMethod method) {
        return sendRequestAsync(url, method, null);
    }

    @Override
    public Promise<Response, Throwable, Void> sendRequestAsync(
            @NonNull String url,
            @NonNull HttpMethod method,
            @Nullable String body) {
        byte[] bodyBytes;
        if (body == null) bodyBytes = new byte[0];
        else bodyBytes = body.getBytes(Charset.forName("utf-8"));
        return sendRequestAsync(url, method, bodyBytes, defaultMediaType);
    }

    @Override
    public Response sendRequest(@NonNull String url, @NonNull HttpMethod method, @Nullable String body)
            throws IOException {
        Ensure.notNullOrEmpty(url, "url");
        Ensure.urlIsPresent(url, "url");

        if (body == null)
            return createCall(url, method, null, null).execute();
        return createCall(
                url,
                method,
                body.getBytes(Charset.forName("utf-8")),
                defaultMediaType).execute();
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
        log.info("Perform HTTP %s request to: %s", method.name(), url);
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
