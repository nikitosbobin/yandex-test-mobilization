package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TranslateActivityPagerAdapter extends FragmentStatePagerAdapter {
    private final String[] titles;
    private final Fragment[] fragments;

    public TranslateActivityPagerAdapter(FragmentManager fm, String[] titles, Fragment[] fragments) {
        super(fm);

        this.titles = titles;
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
        return titles[position];
    }
}
