package com.nikit.bobin.wordstranslate.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nikit.bobin.wordstranslate.helpers.Ensure;
import com.nikit.bobin.wordstranslate.logging.ILog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//Implementation of component which allow app connect to internet using okHttp library
public class HttpSender implements IHttpSender {
    private OkHttpClient client;
    private final String defaultMediaType = "application/x-www-form-urlencoded";
    private ILog log;

    public HttpSender(OkHttpClient httpClient, ILog log) {
        Ensure.notNull(httpClient, "httpClient");
        Ensure.notNull(log, "log");

        client = httpClient;
        this.log = log;
    }

    @Override
    //Execute okHttp call in present thread. Fail when called from ui thread
    public Response sendRequest(
            @NonNull String url,
            @NonNull HttpMethod method,
            @Nullable RequestBody body) throws IOException {
        Ensure.notNull(url, "url");
        Ensure.urlIsPresent(url, "url");
        Ensure.notNull(method, "method");

        Call call = createCall(url, method, body);
        return call.execute();
    }

    //Create okHttp call with necessary arguments processing
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
