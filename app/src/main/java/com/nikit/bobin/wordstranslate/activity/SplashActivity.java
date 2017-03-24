package com.nikit.bobin.wordstranslate.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DefaultDeferredManager;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
    @Inject
    ILanguagesDatabase languagesDatabase;
    @Inject
    ITranslator translator;
    @Inject
    SettingsProvider settingsProvider;
    @Inject
    DeferredManager deferredManager;
    @Inject
    NetworkConnectionInfoProvider connectionInfoProvider;
    @BindView(R.id.splash_screen_status_text)
    TextView splashScreenStatus;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        App.getComponent().injectSplashScreen(this);
        ButterKnife.bind(this);

        uiHandler = new Handler(getMainLooper());
        final Language ui = new Language(Locale.getDefault().getLanguage());
        if (languagesDatabase.isLanguagesSaved(ui)) {
            splashScreenStatus.setText(R.string.starting_app);
            openMainActivityWithDelay(200);
        } else {
            if (connectionInfoProvider.isConnected()) {
                splashScreenStatus.setText(R.string.lang_loading);
                loadLanguagesThenOpenMainActivity(ui);
            } else {
                splashScreenStatus.setText(R.string.no_internet);
            }
        }
    }

    private void loadLanguagesThenOpenMainActivity(final Language ui) {
        translator
                .getLanguagesAsync()
                .then(new DoneCallback<Language[]>() {
                    @Override
                    public void onDone(Language[] result) {
                        if (settingsProvider.isEnableCaching()) {
                            openMainActivity();
                        }
                        languagesDatabase.replaceLanguages(result, ui);
                        //todo: ensure result succeed
                    }
                });
    }

    private void openMainActivity() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void openMainActivityWithDelay(final long delay) {
        deferredManager.when(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                openMainActivity();
            }
        });
    }
}
