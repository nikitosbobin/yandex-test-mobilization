package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.logging.ILog;

import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;

import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
// refactored
// tested
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
            @Nullable RequestBody body) {
        Ensure.notNull(url, "url");
        Ensure.urlIsPresent(url, "url");
        Ensure.notNull(method, "method");

        return createPromise(url, method, body);
    }

    private Promise<Response, Throwable, Void> createPromise(
            final String url,
            final HttpMethod method,
            final RequestBody body) {
        final Call call = createCall(url, method, body);
        return deferredManager.when(new Callable<Response>() {
            public Response call() throws Exception {
                return call.execute();
            }
        });
    }

    private Call createCall(String url, HttpMethod method, RequestBody body) {
        String httpMethod = method.name();
        log.info("Perform HTTP %s request to: %s", httpMethod, url);
        Request.Builder builder = new Request.Builder().url(url);
        if (okhttp3.internal.http.HttpMethod.requiresRequestBody(httpMethod)) {
            if (body == null) {
                log.warn("Method %s requires not null body. Body will be empty byte array", httpMethod);
                body = RequestBody.create(MediaType.parse(defaultMediaType), new byte[0]);
            }
            builder = builder.method(httpMethod, body);
        } else {
            if (body != null)
                log.warn("Method %s did not requires body. Body will not be sent", httpMethod);
        }
        return client.newCall(builder.build());
    }
}
