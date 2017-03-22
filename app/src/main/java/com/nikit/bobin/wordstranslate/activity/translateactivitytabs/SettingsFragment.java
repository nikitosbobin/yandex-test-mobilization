package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.storage.SettingsProvider;

import javax.inject.Inject;

import butterknife.BindView;

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
    private SettingsProvider settingsProvider;

    private void bindListeners() {
        clearHistoryButton.setOnClickListener(this);
        clearFavoritesButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        languagePredictSwitch.setOnCheckedChangeListener(this);
        dictionarySwitch.setOnCheckedChangeListener(this);
        cachingSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    private void clearHistory() {
        throw new Resources.NotFoundException();
    }

    private void clearFavorites() {
        throw new Resources.NotFoundException();
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
                settingsProvider.setEnableDictionary(isChecked);
                break;
            case R.id.show_dictionary_switch:
                settingsProvider.setEnableDictionary(isChecked);
                break;
        }
    }
}
