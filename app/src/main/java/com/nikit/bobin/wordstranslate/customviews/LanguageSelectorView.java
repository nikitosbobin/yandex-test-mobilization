package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LanguageSelectorView extends RelativeLayout implements
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private final int GROUP_ID_FROM_LANGUAGE_MENU = 0;
    private final int GROUP_ID_TO_LANGUAGE_MENU = 1;
    private final String RECENT_LANGUAGES = "recent_translations_languages";

    @BindView(R.id.from_language_view)
    TextView languageFromView;
    @BindView(R.id.to_language_view)
    TextView languageToView;
    @BindView(R.id.language_selector_arrow)
    ImageView arrow;
    @Inject
    ILanguagesDatabase languagesDatabase;
    @Inject
    SharedPreferences preferences;
    @Inject
    Language ui;
    @Inject
    ILog log;

    private ArrayList<Language> recentLanguages;

    private Language languageFrom;
    private Language languageTo;
    private Language[] supportedLanguages;

    private PopupMenu languageFromMenu;
    private PopupMenu languageToMenu;
    private YoYo.AnimationComposer rotateAnimation;
    private YoYo.AnimationComposer fadeInAnimation;
    private OnLanguagesChangeListener onLanguagesChangeListener;

    public LanguageSelectorView(Context context) {
        super(context);
        init();
    }

    public LanguageSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LanguageSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void updateLanguagesPopupMenu() {
        if (supportedLanguages == null)
            return;
        Menu menuFrom = languageFromMenu.getMenu();
        Menu menuTo = languageToMenu.getMenu();
        menuFrom.clear();
        menuTo.clear();
        int offset = recentLanguages.size();
        for (int i = 0; i < offset; ++i) {
            Language next = recentLanguages.get(i);
            int id = offset - i - 1;
            menuFrom.add(GROUP_ID_FROM_LANGUAGE_MENU, id, id, next.getTitle());
            menuTo.add(GROUP_ID_TO_LANGUAGE_MENU, id, id, next.getTitle());
            log.debug("id: %d, order: %d", id, id);
        }
        for (int i = 0; i < supportedLanguages.length; ++i) {
            if (recentLanguages.contains(supportedLanguages[i]))
                continue;
            int id = offset + i;
            String currentLangTitle = supportedLanguages[i].getTitle();
            menuFrom.add(GROUP_ID_FROM_LANGUAGE_MENU, id, id, currentLangTitle);
            menuTo.add(GROUP_ID_TO_LANGUAGE_MENU, id, id, currentLangTitle);
            log.debug("id: %d, order: %d", id, id);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.language_selector_layout, this);
        if (isInEditMode()) return;
        App.getComponent().injectSelectorView(this);
        ButterKnife.bind(this);
        languageFromView.setOnClickListener(this);
        languageToView.setOnClickListener(this);
        arrow.setOnClickListener(this);
        languageFromMenu = new PopupMenu(getContext(), languageFromView);
        languageToMenu = new PopupMenu(getContext(), languageToView);
        languageFromMenu.setOnMenuItemClickListener(this);
        languageToMenu.setOnMenuItemClickListener(this);
        rotateAnimation = YoYo.with(new RotateAnimator()).duration(300);
        fadeInAnimation = YoYo.with(Techniques.FadeIn).duration(300);
        supportedLanguages = languagesDatabase.getLanguages(false);
        loadRecentTranslationsLanguages();
        updateLanguagesPopupMenu();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.from_language_view:
                languageFromMenu.show();
                break;
            case R.id.to_language_view:
                languageToMenu.show();
                break;
            case R.id.language_selector_arrow:
                rotateAnimation.playOn(arrow);
                fadeInAnimation.playOn(languageFromView);
                fadeInAnimation.playOn(languageToView);
                swap();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int groupId = item.getGroupId();
        Language language = supportedLanguages[item.getItemId()];
        switch (groupId) {
            case GROUP_ID_FROM_LANGUAGE_MENU:
                setLanguageFrom(language, true);
                break;
            case GROUP_ID_TO_LANGUAGE_MENU:
                setLanguageTo(language, true);
                break;
        }
        saveNewRecentTranslationLanguage(language);
        updateLanguagesPopupMenu();
        return true;
    }

    // todo: перенести переводы правильно
    private void swap() {
        if (languageFrom == null || languageTo == null)
            return;
        Language tmpTo = languageTo;
        setLanguageTo(languageFrom, false);
        setLanguageFrom(tmpTo, false);
        notifyChange();
    }

    public void setLanguageFrom(Language from, boolean needNotify) {
        Ensure.notNull(from, "from");
        Ensure.inUiThread();

        languageFrom = from;
        languageFromView.setText(languageFrom.getTitle());
        if (needNotify)
            notifyChange();
    }

    private void setLanguageTo(Language to, boolean needNotify) {
        languageTo = to;
        languageToView.setText(languageTo.getTitle());
        if (needNotify)
            notifyChange();
    }

    public Direction getDirection() {
        return new Direction(languageFrom, languageTo);
    }

    public void setOnLanguagesChangeListener(OnLanguagesChangeListener onLanguagesChangeListener) {
        this.onLanguagesChangeListener = onLanguagesChangeListener;
    }

    private void notifyChange() {
        if (onLanguagesChangeListener != null)
            onLanguagesChangeListener.onLanguagesChange(getDirection());
    }

    private void loadRecentTranslationsLanguages() {
        recentLanguages = new ArrayList<>();
        String recentString = preferences.getString(RECENT_LANGUAGES, "");
        if (recentString.length() == 0) {
            if (supportedLanguages.length > 0) {
                setLanguageFrom(supportedLanguages[0], false);
                setLanguageTo(supportedLanguages[0], false);
            }
            return;
        }
        String[] keys = recentString.split(" ");
        for (int i = 0; i < keys.length; ++i) {
            Language language = languagesDatabase.getLanguage(keys[i], ui);
            if (language != null)
                recentLanguages.add(language);
        }
        if (recentLanguages.size() > 0) {
            setLanguageFrom(recentLanguages.get(recentLanguages.size()-1), false);
            setLanguageTo(recentLanguages.get(recentLanguages.size()-1), false);
        }
    }

    private void saveNewRecentTranslationLanguage(Language language) {
        Ensure.notNull(language, "language");
        Ensure.languageHasTitle(language, "language");

        if (recentLanguages == null)
            recentLanguages = new ArrayList<>();

        if (recentLanguages.contains(language))
            return;
        recentLanguages.add(language);
        if (recentLanguages.size() == 4)
            recentLanguages.remove(0);

        SharedPreferences.Editor edit = preferences.edit();
        String stringToSave = Strings.join(recentLanguages, " ");
        edit.putString(RECENT_LANGUAGES, stringToSave);
        edit.apply();
    }

    public interface OnLanguagesChangeListener {
        void onLanguagesChange(Direction direction);
    }
}
