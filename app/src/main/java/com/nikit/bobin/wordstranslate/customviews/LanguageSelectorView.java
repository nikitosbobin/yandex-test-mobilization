package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.storage.ILanguagesDatabase;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LanguageSelectorView extends RelativeLayout implements
        PopupMenu.OnMenuItemClickListener {
    private final int GROUP_ID_FROM_LANGUAGE_MENU = 0;
    private final int GROUP_ID_RECENT_FROM_LANGUAGE_MENU = 1;
    private final int GROUP_ID_TO_LANGUAGE_MENU = 2;
    private final int GROUP_ID_RECENT_TO_LANGUAGE_MENU = 3;
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

    private void init() {
        inflate(getContext(), R.layout.language_selector_layout, this);
        if (isInEditMode()) return;

        IocSetup.getComponent().injectSelectorView(this);
        ButterKnife.bind(this);

        rotateAnimation = YoYo.with(new RotateAnimator()).duration(300);
        fadeInAnimation = YoYo.with(Techniques.FadeIn).duration(300);

        createPopupMenus();
        loadRecentTranslationsLanguages();
        updateRecentPopupMenus();
    }

    private void createPopupMenus() {
        supportedLanguages = languagesDatabase.getLanguages(false);

        languageFromMenu = new PopupMenu(getContext(), languageFromView);
        languageToMenu = new PopupMenu(getContext(), languageToView);

        languageFromMenu.setOnMenuItemClickListener(this);
        languageToMenu.setOnMenuItemClickListener(this);

        Menu menuFrom = languageFromMenu.getMenu();
        Menu menuTo = languageToMenu.getMenu();

        for (int i = 0; i < supportedLanguages.length; ++i) {
            String currentLangTitle = supportedLanguages[i].getTitle();
            menuFrom.add(GROUP_ID_FROM_LANGUAGE_MENU, i, i + 3, currentLangTitle);
            menuTo.add(GROUP_ID_TO_LANGUAGE_MENU, i, i + 3, currentLangTitle);
        }
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
            setLanguageFrom(recentLanguages.get(recentLanguages.size() - 1), false);
            setLanguageTo(recentLanguages.get(recentLanguages.size() - 1), false);
        }
    }

    private void updateRecentPopupMenus() {
        Menu menuFrom = languageFromMenu.getMenu();
        Menu menuTo = languageToMenu.getMenu();

        menuFrom.removeGroup(GROUP_ID_RECENT_FROM_LANGUAGE_MENU);
        menuTo.removeGroup(GROUP_ID_RECENT_TO_LANGUAGE_MENU);

        int recentLanguagesSize = recentLanguages.size();

        for (int i = 0; i < recentLanguagesSize; ++i) {
            Language lang = recentLanguages.get(i);
            menuFrom.add(GROUP_ID_RECENT_FROM_LANGUAGE_MENU, i,
                    recentLanguagesSize - i - 1, lang.getTitle());
            menuTo.add(GROUP_ID_RECENT_TO_LANGUAGE_MENU, i,
                    recentLanguagesSize - i - 1, lang.getTitle());
        }
    }

    @OnClick(R.id.from_language_view)
    public void showFromLanguagePopupMenu() {
        languageToMenu.dismiss();
        languageFromMenu.show();
    }

    @OnClick(R.id.to_language_view)
    public void showToLanguagePopupMenu() {
        languageFromMenu.dismiss();
        languageToMenu.show();
    }

    @OnClick(R.id.language_selector_arrow)
    public void rotateArrowAndSwapLanguages() {
        rotateAnimation.playOn(arrow);
        fadeInAnimation.playOn(languageFromView);
        fadeInAnimation.playOn(languageToView);
        swap();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int groupId = item.getGroupId();
        Language language;
        int itemId = item.getItemId();
        switch (groupId) {
            case GROUP_ID_RECENT_FROM_LANGUAGE_MENU:
                languageFromMenu.dismiss();
                language = recentLanguages.get(itemId);
                setLanguageFrom(language, true);
                break;
            case GROUP_ID_RECENT_TO_LANGUAGE_MENU:
                languageToMenu.dismiss();
                language = recentLanguages.get(itemId);
                setLanguageTo(language, true);
                break;
            case GROUP_ID_FROM_LANGUAGE_MENU:
                languageFromMenu.dismiss();
                language = supportedLanguages[itemId];
                setLanguageFrom(language, true);
                break;
            case GROUP_ID_TO_LANGUAGE_MENU:
                languageToMenu.dismiss();
                language = supportedLanguages[itemId];
                setLanguageTo(language, true);
                break;
            default:
                return false;
        }
        saveNewRecentTranslationLanguage(language);
        updateRecentPopupMenus();
        return true;
    }

    public void setLanguageFrom(Language from, boolean needNotify) {
        Ensure.notNull(from, "from");
        Ensure.inUiThread();

        languageFrom = from;
        languageFromView.setText(languageFrom.getTitle());
        if (needNotify)
            notifyChange();
        saveNewRecentTranslationLanguage(from);
        updateRecentPopupMenus();
    }

    private void saveNewRecentTranslationLanguage(Language language) {
        Ensure.notNull(language, "language");
        Ensure.languageHasTitle(language, "language");

        if (recentLanguages == null)
            recentLanguages = new ArrayList<>();

        if (recentLanguages.contains(language))
            recentLanguages.remove(language);
        recentLanguages.add(language);
        if (recentLanguages.size() == 4)
            recentLanguages.remove(0);

        SharedPreferences.Editor edit = preferences.edit();
        String stringToSave = Strings.join(recentLanguages, " ");
        edit.putString(RECENT_LANGUAGES, stringToSave);
        edit.apply();
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

    public interface OnLanguagesChangeListener {
        void onLanguagesChange(Direction direction);
    }
}
