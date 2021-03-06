package com.nikit.bobin.wordstranslate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DeferredManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

//Activity that manage app loading
public class SplashActivity extends AppCompatActivity {
    @Inject
    ILog log;
    @Inject
    ILanguagesDatabase languagesDatabase;
    @Inject
    ITranslator translator;
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

    private Intent openMainActivityIntent;

    @Override
    //Entry point on activity created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Configuring DI container
        IocSetup.setup(this);

        // Dependency and views injection
        IocSetup.getComponent().inject(this);
        ButterKnife.bind(this);

        openMainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        openMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (languagesDatabase.isLanguagesSaved(ui)) {
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

    /*Load languages with current ui in another thread.
    *   Then save loaded languages and open main activity from ui thread
    */
    private void loadLanguagesThenOpenMainActivity(final Language ui) {
        deferredManager.when(() -> {
            final Language[] languages = translator.getLanguages();
            if (languages == null) {
                log.warn("Loaded languages was null");
                setStatus(R.string.something_wrong);
                return;
            }
            uiHandler.post(() -> {
                languagesDatabase.replaceLanguages(languages, ui);
                startActivity(openMainActivityIntent);
            });
        });
    }

    //Set loading status to display for user (run on ui thread)
    private void setStatus(@StringRes final int stringResource) {
        uiHandler.post(() -> splashScreenStatus.setText(stringResource));
    }

    //Wait delay in another thread, then open main activity in ui thread
    private void openMainActivityWithDelay(final long delay) {
        deferredManager.when(() -> {
            try {
                Thread.sleep(delay);
                openMainActivityInUiThread();
            } catch (Exception e) {
                log.error(
                        "Opening MainActivity with delay: %d ends with error: %s",
                        delay,
                        e.getMessage());
            }
        });
    }

    //Start main activity intent in ui thread
    private void openMainActivityInUiThread() {
        uiHandler.post(() -> startActivity(openMainActivityIntent));
    }
}
