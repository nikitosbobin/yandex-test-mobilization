package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.helpers.Strings;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.HttpMethod;
import com.nikit.bobin.wordstranslate.net.HttpSender;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpSender_Tests {
    private HttpSender httpSender;
    private ILog log;
    private OkHttpClient httpClient;
    private RequestBody requestBody;
    private Call call;

    @Before
    public void setUp() {
        log = mock(ILog.class);
        httpClient = mock(OkHttpClient.class);
        requestBody = mock(RequestBody.class);

        httpSender = new HttpSender(httpClient, log);
        call = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(call);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_http_client_null() {
        new HttpSender(null, log);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_log_null() {
        new HttpSender(httpClient, null);
    }

    @Test(expected = NullPointerException.class)
    public void sendRequest_should_fail_when_uri_null() throws IOException {
        httpSender.sendRequest(null, HttpMethod.GET, requestBody);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendRequest_should_fail_when_uri_empty() throws IOException {
        httpSender.sendRequest(Strings.empty, HttpMethod.GET, requestBody);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendRequest_should_fail_when_uri_is_not_uri() throws IOException {
        httpSender.sendRequest("qwerty", HttpMethod.GET, requestBody);
    }

    @Test(expected = NullPointerException.class)
    public void sendRequest_should_fail_when_method_null() throws IOException {
        httpSender.sendRequest("http://server", null, requestBody);
    }

    @Test
    public void sendRequest_should_execute_call_by_deferred_manager() throws IOException, InterruptedException {
        final Call call = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        httpSender = new HttpSender(httpClient, log);

        httpSender.sendRequest("http://server", HttpMethod.GET, requestBody);

        verify(call).execute();
    }

    @Test
    public void sendRequest_should_create_get_call_with_correct_arguments() throws IOException {
        httpSender.sendRequest("http://server", HttpMethod.GET, null);

        verify(httpClient).newCall(argThat(new ArgumentMatcher<Request>() {
            @Override
            public boolean matches(Object argument) {
                Request request = (Request) argument;
                return request.method().equals("GET")
                        && request.url().toString().equals("http://server/")
                        && request.body() == null;
            }
        }));
        verify(call).execute();
    }

    @Test
    public void sendRequest_should_create_post_call_with_correct_arguments_and_null_request_body()
            throws Exception {
        httpSender.sendRequest("http://server", HttpMethod.POST, null);

        verify(httpClient).newCall(argThat(new ArgumentMatcher<Request>() {
            public boolean matches(Object argument) {
                try {
                    Request request = (Request) argument;
                    return request.method().equals("POST")
                            && request.url().toString().equals("http://server/")
                            && request.body().contentLength() == 0L;
                } catch (IOException e) {
                    return false;
                }
            }
        }));
        verify(call).execute();
    }

    @Test
    public void sendRequest_should_create_post_call_with_correct_arguments() throws Exception {
        httpSender.sendRequest("http://server", HttpMethod.POST, requestBody);

        verify(httpClient).newCall(argThat(new ArgumentMatcher<Request>() {
            public boolean matches(Object argument) {
                Request request = (Request) argument;
                return request.method().equals("POST")
                        && request.url().toString().equals("http://server/")
                        && request.body().equals(requestBody);
            }
        }));
        verify(call).execute();
    }
}
