package com.nikit.bobin.wordstranslate.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.SettingsFragment;
import com.nikit.bobin.wordstranslate.adapters.TranslateActivityPagerAdapter;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslationFragment;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.Language;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

// refactored
public class MainActivity extends AppCompatActivity
        implements MaterialTabListener,
        FavoriteTranslationsFragment.CurrentTranslationChangeListener,
        ViewPager.OnPageChangeListener {
    @BindView(R.id.materialTabHost)
    MaterialTabHost tabHost;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindDrawable(R.drawable.translate)
    Drawable translationTabIcon;
    @BindDrawable(R.drawable.favorite)
    Drawable favoriteTabIcon;
    @BindDrawable(R.drawable.settings)
    Drawable settingsTabIcon;
    @Inject
    NetworkConnectionInfoProvider networkConnectionInfoProvider;
    @Inject
    ITranslator translator;

    private TranslationFragment translationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Dependency and views injection
        ButterKnife.bind(this);
        App.getComponent().injectsMainActivity(this);
        // fragments initializing
        Fragment[] fragments = new Fragment[]{
                new TranslationFragment(),
                new FavoriteTranslationsFragment().setOnCurrentTranslationChangeListener(this),
                new SettingsFragment()
        };
        translationFragment = (TranslationFragment) fragments[0];

        PagerAdapter pagerAdapter = new TranslateActivityPagerAdapter(
                getSupportFragmentManager(),
                fragments);

        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(pagerAdapter);

        tabHost.addTab(tabHost.newTab().setIcon(translationTabIcon).setTabListener(this));
        tabHost.addTab(tabHost.newTab().setIcon(favoriteTabIcon).setTabListener(this));
        tabHost.addTab(tabHost.newTab().setIcon(settingsTabIcon).setTabListener(this));
        networkConnectionInfoProvider.notifyIfNoConnection(viewPager);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onChangeTranslation(long translationId) {
        viewPager.setCurrentItem(0);
        translationFragment.setCurrentTranslation(translationId);
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
        //ignore
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
        //ignore
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //ignore
    }

    @Override
    public void onPageSelected(int position) {
        tabHost.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //ignore
    }
}
