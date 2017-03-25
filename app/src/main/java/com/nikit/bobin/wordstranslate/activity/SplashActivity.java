package com.nikit.bobin.wordstranslate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

// refactored
public class SplashActivity extends AppCompatActivity {
    @Inject
    ILog log;
    @Inject
    ILanguagesDatabase languagesDatabase;
    @Inject
    ITranslator translator;
    @Inject
    SettingsProvider settingsProvider;
    @Inject
    DeferredManager deferredManager;
    @Inject
    Language ui;
    @Inject
    NetworkConnectionInfoProvider connectionInfoProvider;
    @Inject
    Handler uiHandler;
    @BindView(R.id.splash_screen_status_text)
    TextView splashScreenStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Dependency and views injection
        App.getComponent().injectSplashScreen(this);
        ButterKnife.bind(this);

        if (languagesDatabase.isLanguagesSaved(ui)) {
            setStatus(R.string.starting_app);
            openMainActivityWithDelay(200);
        } else {
            if (connectionInfoProvider.isConnectedToInternet()) {
                setStatus(R.string.lang_loading);
                loadLanguagesThenOpenMainActivity(ui);
            } else {
                setStatus(R.string.no_internet);
            }
        }
    }

    private void loadLanguagesThenOpenMainActivity(final Language ui) {
        translator
                .getLanguagesAsync()
                .then(new DoneCallback<Language[]>() {
                    public void onDone(Language[] result) {
                        if (result == null) {
                            log.warn("Loaded languages was null");
                            setStatus(R.string.something_wrong);
                        }
                        if (settingsProvider.isEnableCaching()) {
                            openMainActivity();
                        }
                        languagesDatabase.replaceLanguages(result, ui);
                    }
                });
    }

    private void openMainActivity() {
        uiHandler.post(new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void openMainActivityWithDelay(final long delay) {
        deferredManager.when(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(delay);
                    openMainActivity();
                } catch (Exception e) {
                    log.error("Opening MainActivity with delay: %d ends with error: %s", delay, e.getMessage());
                }
            }
        });
    }

    private void setStatus(@StringRes int stringResource) {
        splashScreenStatus.setText(stringResource);
    }
}
