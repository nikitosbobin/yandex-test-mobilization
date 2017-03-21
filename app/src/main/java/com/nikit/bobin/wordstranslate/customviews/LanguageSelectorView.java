package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LanguageSelectorView extends RelativeLayout implements
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private Language languageFrom;
    private Language languageTo;
    private static Language[] supportedLanguages;
    @BindView(R.id.from_language_view) TextView languageFromView;
    @BindView(R.id.to_language_view) TextView languageToView;
    @BindView(R.id.language_selector_arrow) ImageView arrow;
    private PopupMenu languageFromMenu;
    private PopupMenu languageToMenu;
    private static LanguageSelectorView instance;
    private final int GROUP_ID_FROM_LANGUAGE_MENU = 0;
    private final int GROUP_ID_TO_LANGUAGE_MENU = 1;
    private YoYo.AnimationComposer rotateAnimation;
    private YoYo.AnimationComposer fadeInAnimation;
    private Runnable swapListener;

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

    public static void setSupportedLanguages(Language[] supportedLanguages) {
        Ensure.inUiThread();
        Ensure.notNull(supportedLanguages, "supportedLanguages");

        Log.d("logs", supportedLanguages.length + "");
        supportedLanguages = supportedLanguages.clone();
        Arrays.sort(supportedLanguages);
        LanguageSelectorView.supportedLanguages = supportedLanguages;
        if (instance != null)
            instance.updateSupportedLangs();
    }

    private void updateSupportedLangs() {
        if (supportedLanguages == null)
            return;
        Menu menuFrom = languageFromMenu.getMenu();
        Menu menuTo = languageToMenu.getMenu();
        menuFrom.clear();
        menuTo.clear();
        if (supportedLanguages.length > 0) {
            setLanguageFrom(supportedLanguages[0]);
            setLanguageTo(supportedLanguages[0]);
        }
        for (int i = 0; i < supportedLanguages.length; ++i) {
            String currentLangTitle = supportedLanguages[i].getTitle();
            menuFrom.add(GROUP_ID_FROM_LANGUAGE_MENU, i, i, currentLangTitle);
            menuTo.add(GROUP_ID_TO_LANGUAGE_MENU, i, i, currentLangTitle);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.language_selector_layout, this);
        ButterKnife.bind(this);
        languageFromView.setOnClickListener(this);
        languageToView.setOnClickListener(this);
        arrow.setOnClickListener(this);
        languageFromMenu = new PopupMenu(getContext(), languageFromView);
        languageToMenu = new PopupMenu(getContext(), languageToView);
        languageFromMenu.setOnMenuItemClickListener(this);
        languageToMenu.setOnMenuItemClickListener(this);
        instance = this;
        updateSupportedLangs();
        rotateAnimation = YoYo.with(new RotateAnimator()).duration(300);
        fadeInAnimation = YoYo.with(Techniques.FadeIn).duration(300);
    }

    public void clearLangs() {
        languageFrom = null;
        languageTo = null;
        invalidate();
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
                if (swapListener != null)
                    swapListener.run();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int groupId = item.getGroupId();
        switch (groupId) {
            case GROUP_ID_FROM_LANGUAGE_MENU:
                setLanguageFrom(supportedLanguages[item.getItemId()]);
                break;
            case GROUP_ID_TO_LANGUAGE_MENU:
                setLanguageTo(supportedLanguages[item.getItemId()]);
                break;
        }
        return true;
    }

    private void swap() {
        if (languageFrom == null || languageTo == null)
            return;
        Language tmpTo = languageTo;
        setLanguageTo(languageFrom);
        setLanguageFrom(tmpTo);
    }

    private void setLanguageFrom(Language from) {
        languageFrom = from;
        languageFromView.setText(languageFrom.getTitle());
    }

    private void setLanguageTo(Language to) {
        languageTo = to;
        languageToView.setText(languageTo.getTitle());
    }

    public Direction getDirection() {
        return new Direction(languageFrom, languageTo);
    }

    public void setOnSwapListener(Runnable onSwapListener) {
        swapListener = onSwapListener;
    }
}
