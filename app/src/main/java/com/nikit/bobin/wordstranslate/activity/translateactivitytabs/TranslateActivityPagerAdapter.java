package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TranslateActivityPagerAdapter extends FragmentStatePagerAdapter {
    private final ToolBarControlFragment[] fragments;

    public TranslateActivityPagerAdapter(FragmentManager fm, ToolBarControlFragment[] fragments) {
        super(fm);

        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getTitle();
    }
}
