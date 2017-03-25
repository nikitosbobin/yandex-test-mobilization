package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

// refactored
public class SettingsFragment extends Fragment {
    @BindView(R.id.determine_lang_switch)
    Switch languagePredictSwitch;
    @BindView(R.id.show_dictionary_switch)
    Switch dictionarySwitch;
    @BindView(R.id.caching_switch)
    Switch cachingSwitch;
    @BindView(R.id.clear_history_button)
    Button clearHistoryButton;
    @BindView(R.id.clear_favorite_button)
    Button clearFavoritesButton;
    @BindView(R.id.about_button)
    Button aboutButton;

    @Inject
    ITranslationsDatabase translationsDatabase;
    @Inject
    SettingsProvider settingsProvider;

    private MaterialDialog clearHistoryDialog;
    private MaterialDialog clearFavoriteDialog;
    private MaterialDialog aboutDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Dependency and views injection
        App.getComponent().injectSettingsFragment(this);
        ButterKnife.bind(this, view);
        // initializing switches values
        languagePredictSwitch.setChecked(settingsProvider.isEnableLangPrediction());
        dictionarySwitch.setChecked(settingsProvider.isEnableDictionary());
        cachingSwitch.setChecked(settingsProvider.isEnableCaching());

        return view;
    }

    @OnClick(R.id.clear_history_button)
    public void clearHistory() {
        if (clearHistoryDialog == null)
            clearHistoryDialog = createClearHistoryDialog();
        clearHistoryDialog.show();
    }

    @OnClick(R.id.clear_favorite_button)
    public void clearFavorites() {
        if (clearFavoriteDialog == null)
            clearFavoriteDialog = createClearFavoritesDialog();
        clearFavoriteDialog.show();
    }

    @OnClick(R.id.about_button)
    public void openAboutDialog() {
        if (aboutDialog == null)
            aboutDialog = createAboutDialog();
        aboutDialog.show();
    }

    @OnCheckedChanged(R.id.caching_switch)
    public void setCachingState(boolean isChecked) {
        settingsProvider.setEnableCaching(isChecked);
    }

    @OnCheckedChanged(R.id.determine_lang_switch)
    public void setPredictionState(boolean isChecked) {
        settingsProvider.setEnableLangPrediction(isChecked);
    }

    @OnCheckedChanged(R.id.show_dictionary_switch)
    public void setDictionaryState(boolean isChecked) {
        settingsProvider.setEnableDictionary(isChecked);
    }

    private MaterialDialog createClearHistoryDialog() {
        return new MaterialDialog.Builder(getContext())
                .title(R.string.confirm_delete)
                .content(R.string.history_delete_dialog)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        translationsDatabase.deleteAllTranslations();
                    }
                })
                .build();
    }

    private MaterialDialog createClearFavoritesDialog() {
        return new MaterialDialog.Builder(getContext())
                .title(R.string.confirm_delete)
                .content(R.string.favorite_delete_dialog)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        translationsDatabase.deleteAllFavorites();
                    }
                })
                .build();
    }

    private MaterialDialog createAboutDialog() {
        return new MaterialDialog.Builder(getContext())
                .title(R.string.app_name)
                .content(R.string.about_description)
                .negativeText(R.string.cancel)
                .build();
    }
}
