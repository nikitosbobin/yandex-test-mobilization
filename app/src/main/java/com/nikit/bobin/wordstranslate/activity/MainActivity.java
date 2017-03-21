package com.nikit.bobin.wordstranslate.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.SettingsFragment;
import com.nikit.bobin.wordstranslate.adapters.TranslateActivityPagerAdapter;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends AppCompatActivity implements MaterialTabListener {
    @BindView(R.id.materialTabHost) MaterialTabHost tabHost;
    @BindView(R.id.viewPager) ViewPager viewPager;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        fragments = new Fragment[] {
                new TranslationFragment(),
                new FavoriteTranslationsFragment(),
                new SettingsFragment()
        };

        PagerAdapter pagerAdapter = new TranslateActivityPagerAdapter(
                getSupportFragmentManager(),
                fragments);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        viewPager.setAdapter(pagerAdapter);
        tabHost.addTab(tabHost.newTab()
                .setIcon(getResources().getDrawable(R.drawable.translate))
                .setTabListener(this));
        tabHost.addTab(tabHost.newTab()
                .setIcon(getResources().getDrawable(R.drawable.favorite))
                .setTabListener(this));
        tabHost.addTab(tabHost.newTab()
                .setIcon(getResources().getDrawable(R.drawable.settings))
                .setTabListener(this));
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
        //ignore
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
        //ignore
    }
}
