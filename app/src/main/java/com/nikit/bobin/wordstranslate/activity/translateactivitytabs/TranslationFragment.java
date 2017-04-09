package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.Promise;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TranslationFragment extends Fragment
        implements LanguageSelectorView.OnLanguagesChangeListener,
        LanguageSelectorView.OnLanguagesSwapListener {
    private final String DIRECTION_SAVED_FLAG = "direction-saved";
    private final String DIRECTION = "direction";

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
    DeferredManager deferredManager;
    @Inject
    SettingsProvider settingsProvider;
    @Inject
    NetworkConnectionInfoProvider networkConnectionInfoProvider;
    @Inject
    AnimationsFactory animationsFactory;
    @Inject
    Language ui;
    @Inject
    Handler uiHandler;
    private TranslatedText currentTranslation;
    private Promise currentPromise;
    private YoYo.AnimationComposer translationCardOutAnimation;
    private YoYo.AnimationComposer translationCardInAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        // Dependency and views injection
        IocSetup.getComponent().inject(this);
        ButterKnife.bind(this, view);

        translationCardOutAnimation = animationsFactory
                .createSlideOutDownAnimation(300)
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        translationCard.setVisibility(View.GONE);
                    }
                });
        translationCardInAnimation = animationsFactory.createSlideInUpAnimation(300);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(DIRECTION_SAVED_FLAG, false)) {
                String serializedDirection = savedInstanceState.getString(DIRECTION);
                Direction direction = Direction.parseFullSerialized(serializedDirection);
                selectorView.setDirection(direction, true);
            }
        }

        selectorView.setOnLanguagesChangeListener(this);
        selectorView.setOnLanguagesSwapListener(this);
        return view;
    }

    @OnTextChanged(R.id.translation_input)
    public void onTextChanged(CharSequence s) {
        if (!networkConnectionInfoProvider.isConnectedToInternet()) {
            Snackbar.make(input, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (s == null || s.length() == 0) {
            clearButton.setVisibility(View.GONE);
            clearTranslationCard();
            return;
        }
        clearButton.setVisibility(View.VISIBLE);
        Translation targetTranslation = new Translation(
                s.toString().trim(),
                selectorView.getDirection());
        if (currentTranslation == null ||
                !targetTranslation.equals(currentTranslation.getTranslation())) {
            doTranslation(targetTranslation);
        }
    }

    private void clearTranslationCard() {
        translationCardOutAnimation.playOn(translationCard);
        currentTranslation = null;
    }

    public void fillTranslationCard(final TranslatedText translatedText) {
        uiHandler.post(new Runnable() {
            public void run() {
                translationCard.fillTranslation(translatedText);
                if (translationCard.getVisibility() != View.VISIBLE) {
                    translationCard.setVisibility(View.VISIBLE);
                    translationCardInAnimation.playOn(translationCard);
                }
            }
        });
    }

    private void doTranslation(final Translation targetTranslation) {
        if (currentPromise != null
                && !currentPromise.isResolved()
                && !currentPromise.isRejected())
            return;
        currentPromise = deferredManager
                .when(detectLanguage(targetTranslation))
                .then(translateTextContinuation(targetTranslation))
                .then(handleTranslationContinuation(targetTranslation))
                .then(loadLookupContinuation(targetTranslation));
    }

    @NonNull
    private Runnable detectLanguage(final Translation targetTranslation) {
        return new Runnable() {
            public void run() {
                if (needPredict()) {
                    final Language detection = translator
                            .detectLanguage(targetTranslation.getOriginalText());
                    if (detection != null)
                        setLanguageFrom(detection);
                }
            }
        };
    }

    private boolean needPredict() {
        return settingsProvider.isEnableLangPrediction();
    }

    private void setLanguageFrom(final Language from) {
        uiHandler.post(new Runnable() {
            public void run() {
                selectorView.setLanguageFrom(from, false);
            }
        });
    }

    @NonNull
    private DoneFilter<Void, TranslatedText> translateTextContinuation(final Translation targetTranslation) {
        return new DoneFilter<Void, TranslatedText>() {
            public TranslatedText filterDone(Void result) {
                return translator.translate(targetTranslation);
            }
        };
    }

    @NonNull
    private DoneFilter<TranslatedText, Boolean> handleTranslationContinuation(final Translation targetTranslation) {
        return new DoneFilter<TranslatedText, Boolean>() {
            public Boolean filterDone(TranslatedText result) {
                if (!result.isSuccess()) return false;
                currentTranslation = result;
                fillTranslationCard(result);
                if (needDictionary() && targetTranslation.getWordCount() == 1)
                    return true;
                setLookup(WordLookup.empty(result.getTranslation()));
                return false;
            }
        };
    }

    private boolean needDictionary() {
        return settingsProvider.isEnableDictionary();
    }

    private void setLookup(final WordLookup lookup) {
        uiHandler.post(new Runnable() {
            public void run() {
                translationCard.setLookup(lookup);
            }
        });
    }

    @NonNull
    private DoneCallback<Boolean> loadLookupContinuation(final Translation targetTranslation) {
        return new DoneCallback<Boolean>() {
            public void onDone(Boolean result) {
                if (!result) return;
                final WordLookup wordLookup = translator.getWordLookup(targetTranslation);
                if (wordLookup != null)
                    setLookup(wordLookup);
            }
        };
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
            translationsDatabase.save(currentTranslation);
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

    @Override
    public void onLanguagesChange(Direction direction) {
        Editable text = input.getText();
        if (direction == null || text == null || text.length() == 0) return;
        Translation targetTranslation = new Translation(text.toString(), direction);
        doTranslation(targetTranslation);
    }

    @Override
    public void onLanguagesSwap(Direction newSwappedDirection) {
        if (currentTranslation != null) {
            input.setText(currentTranslation.getTranslatedText());
            onLanguagesChange(newSwappedDirection);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Direction direction = selectorView.getDirection();
        outState.putString(DIRECTION, direction.fullSerialize());
        outState.putBoolean(DIRECTION_SAVED_FLAG, true);
    }
}
