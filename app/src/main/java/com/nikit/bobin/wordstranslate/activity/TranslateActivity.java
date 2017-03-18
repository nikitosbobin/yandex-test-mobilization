package com.nikit.bobin.wordstranslate.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.history.IStorage;
import com.nikit.bobin.wordstranslate.history.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TranslateActivity extends AppCompatActivity implements TextWatcher {
    @Inject ILog log;
    @Inject ITranslator translator;
    @Inject IStorage<TranslatedText> historyStorage;
    @BindView(R.id.history_list) ListView historyList;
    @BindView(R.id.editText) EditText input;
    @BindView(R.id.lang_selector) LanguageSelectorView selectorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        // dependency injection
        App.getComponent().injectsMainActivity(this);
        ButterKnife.bind(this);

        TranslationHistoryAdapter adapter = new TranslationHistoryAdapter(this, historyStorage);
        historyList.setAdapter(adapter);
        input.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //ignore
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0)
            return;
        translator
                .translateAsync(new Translation(input.getText().toString(), selectorView.getDirection()))
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(final TranslatedText result) {
                        if (!result.isSuccess()) {
                            return;
                        }
                        log.info(result.getTranslatedText());
                    }
                });
    }

    @Override
    public void afterTextChanged(Editable s) {
        //ignore
    }
}
