package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.content.res.Resources;
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

public class SettingsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
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

    public SettingsFragment() {
        super();
    }

    private void initControls() {
        clearHistoryButton.setOnClickListener(this);
        clearFavoritesButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);

        languagePredictSwitch.setOnCheckedChangeListener(this);
        dictionarySwitch.setOnCheckedChangeListener(this);
        cachingSwitch.setOnCheckedChangeListener(this);

        languagePredictSwitch.setChecked(settingsProvider.isEnableLangPrediction());
        dictionarySwitch.setChecked(settingsProvider.isEnableDictionary());
        cachingSwitch.setChecked(settingsProvider.isEnableCaching());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        App.getComponent().injectSettingsFragment(this);
        ButterKnife.bind(this, view);
        initControls();
        return view;
    }

    private void clearHistory() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.confirm_delete)
                .content(R.string.history_delete_dialog)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        translationsDatabase.deleteAllTranslations();
                    }
                })
                .show();
    }

    private void clearFavorites() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.confirm_delete)
                .content(R.string.favorite_delete_dialog)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        translationsDatabase.deleteAllFavorites();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.about_button:
                break;
            case R.id.clear_favorite_button:
                clearFavorites();
                break;
            case R.id.clear_history_button:
                clearHistory();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.caching_switch:
                settingsProvider.setEnableCaching(isChecked);
                break;
            case R.id.determine_lang_switch:
                settingsProvider.setEnableLangPrediction(isChecked);
                break;
            case R.id.show_dictionary_switch:
                settingsProvider.setEnableDictionary(isChecked);
                break;
        }
    }
}
