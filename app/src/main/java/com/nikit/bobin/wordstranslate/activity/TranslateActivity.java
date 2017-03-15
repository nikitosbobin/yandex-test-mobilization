package com.nikit.bobin.wordstranslate.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.logging.SimpleLogger;
import com.nikit.bobin.wordstranslate.net.HttpSender;
import com.nikit.bobin.wordstranslate.net.IHttpSender;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.YandexTranslator;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.impl.DefaultDeferredManager;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TranslateActivity extends AppCompatActivity {
    private ILog log;
    private IHttpSender httpSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        log = new SimpleLogger();
        DefaultDeferredManager deferredManager = new DefaultDeferredManager();
        httpSender = new HttpSender(deferredManager);
        ITranslator translator = new YandexTranslator(deferredManager, httpSender, true, log);

        translator.getSupportedLangsAsync("ru")
                .then(new DoneCallback<HashMap<String, String>>() {
                    @Override
                    public void onDone(HashMap<String, String> result) {
                        log.info("fdfdsf");
                    }
                })
                .fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        log.info("fail");
                    }
                });

    }
}
