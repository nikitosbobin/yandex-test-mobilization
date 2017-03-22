package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationFragment extends Fragment implements TextWatcher, View.OnClickListener {
    @BindView(R.id.translation_input)
    EditText input;
    @BindView(R.id.clear_button)
    ImageView clearButton;
    @BindView(R.id.direction_label)
    TextView direction;
    @BindView(R.id.original_text_label)
    TextView original;
    @BindView(R.id.translated_text_label)
    TextView translated;
    @BindView(R.id.current_translation)
    CardView translationCard;
    @Inject
    ITranslator translator;
    @Inject
    ILog log;
    @BindView(R.id.lang_selector)
    LanguageSelectorView selectorView;
    @Inject
    ITranslationsDatabase translationsDatabase;
    private TranslatedText currentTranslation;
    private Handler uiHandler;

    public TranslationFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        App.getComponent().injectTranslationFragment(this);
        ButterKnife.bind(this, view);

        input.addTextChangedListener(this);
        clearButton.setOnClickListener(this);

        selectorView.setOnSwapListener(new Runnable() {
            @Override
            public void run() {
                Editable text = input.getText();
                onTextChanged(text, 0, 0, text.length());
            }
        });
        uiHandler = new Handler(getContext().getMainLooper());
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //ignore
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            clearButton.setVisibility(View.GONE);
            clearTranslationCard();
            return;
        }
        clearButton.setVisibility(View.VISIBLE);
        Translation targetTranslation = new Translation(s.toString(), selectorView.getDirection());
        if (currentTranslation == null || !targetTranslation.equals(currentTranslation.getTranslation()))
            PerformTranslation(targetTranslation);
    }

    private void PerformTranslation(Translation targetTranslation) {
        translator
                .translateAsync(targetTranslation)
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(final TranslatedText result) {
                        if (!result.isSuccess()) return;
                        currentTranslation = result;
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                fillTranslationCard(result);
                            }
                        });
                    }
                });
    }

    @Override
    public void afterTextChanged(Editable s) {
        //ignore
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_button) {
            commitTranslation();
            input.setText("");
            clearButton.setVisibility(View.GONE);
        }
    }

    private void commitTranslation() {
        TranslatedText[] items = translationsDatabase.getAllTranslations(true);
        if (items.length == 0 || (items.length > 0 && !items[0].equals(currentTranslation))) {
            translationsDatabase.addOrUpdate(currentTranslation);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, Menu.NONE, "Delete");
    }

    private void clearTranslationCard() {
        translated.setText("");
        original.setText("");
        direction.setText("");
        YoYo.with(Techniques.SlideOutDown)
                .duration(300)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        translationCard.setVisibility(View.GONE);
                    }
                })
                .playOn(translationCard);
    }

    public void fillTranslationCard(TranslatedText translatedText) {
        translated.setText(translatedText.getTranslatedText());
        original.setText(translatedText.getTranslation().getOriginalText());
        direction.setText(translatedText.getTranslation().getDirection().toString().toUpperCase());
        if (translationCard.getVisibility() != View.VISIBLE) {
            translationCard.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp)
                    .duration(300)
                    .playOn(translationCard);
        }
    }

    public void setCurrentTranslation(long translationId) {
        TranslatedText item = translationsDatabase.getById(translationId);
        if (item != null){
            currentTranslation = item;
            fillTranslationCard(item);
            input.setText(item.getTranslation().getOriginalText());
        }
    }
}
