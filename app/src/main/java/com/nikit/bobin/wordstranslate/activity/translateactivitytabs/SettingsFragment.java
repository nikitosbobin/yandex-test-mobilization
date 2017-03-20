package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nikit.bobin.wordstranslate.R;

public class SettingsFragment extends ToolBarControlFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    CharSequence getTitle() {
        return "Settings";
    }
}
