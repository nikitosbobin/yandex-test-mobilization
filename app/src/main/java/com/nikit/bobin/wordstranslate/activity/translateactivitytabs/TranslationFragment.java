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

import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.AnimationsFactory;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.customviews.TranslationCard;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TranslationFragment extends Fragment
        implements LanguageSelectorView.OnLanguagesChangeListener,
        LanguageSelectorView.OnLanguagesSwapListener {
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
    ILanguagesDatabase languagesDatabase;
    @Inject
    SettingsProvider settingsProvider;
    @Inject
    NetworkConnectionInfoProvider networkConnectionInfoProvider;
    @Inject
    AnimationsFactory animationsFactory;
    @Inject
    Language ui;
    private TranslatedText currentTranslation;
    private Promise<TranslatedText, Throwable, Void> currentTranslationPromise;
    private Promise<Language, Throwable, Void> currentPredictionPromise;
    private Promise<WordLookup, Throwable, Void> currentLookupPromise;
    private YoYo.AnimationComposer translationCardOutAnimation;
    private YoYo.AnimationComposer translationCardInAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        // Dependency and views injection
        IocSetup.getComponent().injectTranslationFragment(this);
        ButterKnife.bind(this, view);

        translationCardOutAnimation = animationsFactory
                .createSlideOutDownAnimation(300)
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        translationCard.setVisibility(View.GONE);
                    }
                });
        translationCardInAnimation = animationsFactory
                .createSlideInUpAnimation(300);

        selectorView.setOnLanguagesChangeListener(this);
        selectorView.setOnLanguagesSwapListener(this);
        return view;
    }

    @OnTextChanged(R.id.translation_input)
    public void onTextChanged(CharSequence s) {
        networkConnectionInfoProvider.notifyIfNoConnection(input);
        if (s == null || s.length() == 0) {
            clearButton.setVisibility(View.GONE);
            clearTranslationCard();
            return;
        }
        clearButton.setVisibility(View.VISIBLE);
        Translation targetTranslation = new Translation(s.toString().trim(), selectorView.getDirection());
        if (currentTranslation == null || !targetTranslation.equals(currentTranslation.getTranslation())) {
            tryDetectLang(targetTranslation);
            performTranslation(targetTranslation);
        }
    }

    private void performTranslation(final Translation targetTranslation) {
        if (currentTranslationPromise != null
                && !currentTranslationPromise.isResolved()
                && !currentTranslationPromise.isRejected())
            return;
        currentTranslationPromise = translator
                .translateAsync(targetTranslation)
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(final TranslatedText result) {
                        if (!result.isSuccess()) return;
                        currentTranslation = result;
                        fillTranslationCard(result);
                        if (!needDictionary() || targetTranslation.getWordCount() != 1)
                            translationCard.setLookup(WordLookup.empty(result.getTranslation()));
                        else
                            tryLoadLookup(result);
                    }
                });
    }

    private void tryLoadLookup(TranslatedText result) {
        if (currentLookupPromise != null
                && !currentLookupPromise.isResolved()
                && !currentLookupPromise.isRejected())
            return;
        currentLookupPromise = translator
                .getWordLookupAsync(result.getTranslation())
                .then(new DoneCallback<WordLookup>() {
                    public void onDone(final WordLookup result) {
                        if (result != null)
                            translationCard.setLookup(result);
                    }
                });
    }

    private void tryDetectLang(Translation targetTranslation) {
        if ((currentPredictionPromise != null
                && !currentPredictionPromise.isResolved()
                && !currentPredictionPromise.isRejected())
                || !needPredict() || targetTranslation.getWordCount() != 1)
            return;
        currentPredictionPromise = translator
                .detectLanguageAsync(targetTranslation.getOriginalText())
                .then(new DoneCallback<Language>() {
                    public void onDone(final Language result) {
                        if (result != null)
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
        translationCardOutAnimation.playOn(translationCard);
    }

    public void fillTranslationCard(TranslatedText translatedText) {
        translationCard.fillTranslation(translatedText);
        if (translationCard.getVisibility() != View.VISIBLE) {
            translationCard.setVisibility(View.VISIBLE);
            translationCardInAnimation.playOn(translationCard);
        }
    }

    public void setCurrentTranslation(long translationId) {
        TranslatedText item = translationsDatabase.getById(translationId);
        if (item != null) {
            currentTranslation = item;
            fillTranslationCard(item);
            input.setText(item.getTranslation().getOriginalText());
            Language to = item.getTranslation().getDirection().getTo();
            to = languagesDatabase.getLanguage(to.getKey(), ui);
            if (to != null)
                selectorView.setLanguageTo(to, false);
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
        if (direction == null || text == null || text.length() == 0) return;
        Translation targetTranslation = new Translation(text.toString(), direction);
        performTranslation(targetTranslation);
    }

    @Override
    public void onLanguagesSwap(Direction newSwappedDirection) {
        if (currentTranslation != null) {
            input.setText(currentTranslation.getTranslatedText());
            onLanguagesChange(newSwappedDirection);
        }
    }
}
