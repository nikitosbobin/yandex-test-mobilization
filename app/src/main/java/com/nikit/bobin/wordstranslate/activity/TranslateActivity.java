package com.nikit.bobin.wordstranslate.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.logging.*;
import com.nikit.bobin.wordstranslate.net.HttpSender;
import com.nikit.bobin.wordstranslate.translating.*;
import com.nikit.bobin.wordstranslate.translating.models.*;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.impl.DefaultDeferredManager;


public class TranslateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        ILog log = new SimpleLogger();
        DefaultDeferredManager deferredManager = new DefaultDeferredManager();
        HttpSender httpSender = new HttpSender(deferredManager);
        IYandexResponseExtractor extractor = new YandexResponseExtractor();
        IYandexRestApiUriFactory factory = new YandexRestApiUriFactory();

        ITranslator translator = new YandexTranslator(
                deferredManager,
                httpSender,
                true,
                log,
                new Language("ru"),
                factory,
                extractor);

        /*translator
                .translateAsync(new Translation("привет мир", Direction.parse("ru-fr")))
                .then(new DoneCallback<TranslatedText>() {
                    @Override
                    public void onDone(TranslatedText result) {

                        int y = 0;
                    }
                });*/

        translator.getLanguagesAsync()
                .then(new DoneCallback<Language[]>() {
                    @Override
                    public void onDone(Language[] result) {
                        int y = 0;
                    }
                })
                .fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        int y = 0;
                    }
                });
    }
}
