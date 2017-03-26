package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.customviews.TranslationCard;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TranslationFragment extends Fragment
        implements LanguageSelectorView.OnLanguagesChangeListener {
    @BindView(R.id.translation_input)
    EditText input;
    @BindView(R.id.clear_button)
    ImageView clearButton;
    @BindView(R.id.current_translation)
    TranslationCard translationCard;
    @BindView(R.id.lang_selector)
    LanguageSelectorView selectorView;
    @Inject
    ITranslator translator;
    @Inject
    ILog log;
    @Inject
    ITranslationsDatabase translationsDatabase;
    @Inject
    SettingsProvider settingsProvider;
    @Inject
    NetworkConnectionInfoProvider networkConnectionInfoProvider;
    private TranslatedText currentTranslation;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        // Dependency and views injection
        App.getComponent().injectTranslationFragment(this);
        ButterKnife.bind(this, view);

        selectorView.setOnLanguagesChangeListener(this);
        return view;
    }

    @OnTextChanged(R.id.translation_input)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        networkConnectionInfoProvider.notifyIfNoConnection(input);
        if (s.length() == 0) {
            clearButton.setVisibility(View.GONE);
            clearTranslationCard();
            return;
        }
        log.debug("s: %s start: %d before %d count: %d", s.toString(), start, before, count);
        clearButton.setVisibility(View.VISIBLE);
        Translation targetTranslation = new Translation(s.toString(), selectorView.getDirection());
        if (currentTranslation == null || !targetTranslation.equals(currentTranslation.getTranslation())) {
            tryDetectLang(targetTranslation);
            performTranslation(targetTranslation);
        }
    }

    private void performTranslation(final Translation targetTranslation) {
        translator
                .translateAsync(targetTranslation)
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(final TranslatedText result) {
                        if (!result.isSuccess()) return;
                        currentTranslation = result;
                        fillTranslationCard(result);
                    }
                })
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(TranslatedText result) {
                        if (!needDictionary() || targetTranslation.getWordCount() != 1) {
                            translationCard.setLookup(WordLookup.empty());
                            return;
                        }
                        tryLoadLookup(result);
                    }
                });
    }

    private void tryLoadLookup(TranslatedText result) {
        translator
                .getWordLookupAsync(result.getTranslation())
                .then(new DoneCallback<WordLookup>() {
                    public void onDone(final WordLookup result) {
                        if (result != null)
                            translationCard.setLookup(result);
                    }
                });
    }

    private void tryDetectLang(Translation targetTranslation) {
        if (!needPredict() || targetTranslation.getWordCount() != 1)
            return;
        translator
                .detectLanguageAsync(targetTranslation.getOriginalText())
                .then(new DoneCallback<Language>() {
                    public void onDone(final Language result) {
                        selectorView.setLanguageFrom(result, true);
                    }
                });
    }

    @OnClick(R.id.clear_button)
    public void clearInputView() {
        commitTranslation();
        input.setText(Strings.empty);
        clearButton.setVisibility(View.GONE);
    }

    private void commitTranslation() {
        if (currentTranslation == null) return;
        TranslatedText[] items = translationsDatabase.getAllTranslations(true);
        if (items.length == 0 || (items.length > 0 && !items[0].equals(currentTranslation))) {
            translationsDatabase.addOrUpdate(currentTranslation);
        }
    }

    private void clearTranslationCard() {
        YoYo.with(Techniques.SlideOutDown)
                .duration(300)
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        translationCard.setVisibility(View.GONE);
                    }
                })
                .playOn(translationCard);
    }

    public void fillTranslationCard(TranslatedText translatedText) {
        translationCard.fillTranslation(translatedText);
        if (translationCard.getVisibility() != View.VISIBLE) {
            translationCard.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp)
                    .duration(300)
                    .playOn(translationCard);
        }
    }

    public void setCurrentTranslation(long translationId) {
        TranslatedText item = translationsDatabase.getById(translationId);
        if (item != null) {
            currentTranslation = item;
            fillTranslationCard(item);
            input.setText(item.getTranslation().getOriginalText());
        }
    }

    private boolean needPredict() {
        return settingsProvider.isEnableLangPrediction();
    }

    private boolean needDictionary() {
        return settingsProvider.isEnableDictionary();
    }

    @Override
    public void onLanguagesChange(Direction direction) {
        Editable text = input.getText();
        if (text.length() == 0) return;
        Translation targetTranslation = new Translation(text.toString(), direction);
        performTranslation(targetTranslation);
    }
}
