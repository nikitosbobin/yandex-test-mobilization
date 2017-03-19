package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.history.IStorage;
import com.nikit.bobin.wordstranslate.history.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationFragment extends Fragment implements TextWatcher {
    @BindView(R.id.history_list) ListView historyList;
    @BindView(R.id.editText) EditText input;
    @Inject ITranslator translator;
    @Inject IStorage<TranslatedText> historyStorage;
    @Inject ILog log;

    private LanguageSelectorView selectorView;

    public TranslationFragment() {
        super();
    }

    public TranslationFragment setSelectorView(LanguageSelectorView selectorView) {
        this.selectorView = selectorView;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        App.getComponent().injectTranslationFragment(this);
        ButterKnife.bind(this, view);

        TranslationHistoryAdapter adapter = new TranslationHistoryAdapter(getContext(), historyStorage);
        historyList.setAdapter(adapter);
        input.addTextChangedListener(this);
        return view;
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
