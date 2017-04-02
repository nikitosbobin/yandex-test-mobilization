package com.nikit.bobin.wordstranslate.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.SettingsFragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslationFragment;
import com.nikit.bobin.wordstranslate.adapters.TranslateActivityPagerAdapter;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

// refactored
public class MainActivity extends AppCompatActivity
        implements FavoriteTranslationsFragment.CurrentTranslationChangeListener,
        ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.activity_main_tabs)
    BottomNavigationView bottomNavigationView;
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
    @Inject
    ILog log;
    private InputMethodManager inputMethodManager;
    private TranslationFragment translationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Dependency and views injection
        ButterKnife.bind(this);
        IocSetup.getComponent().injectsMainActivity(this);
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

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        networkConnectionInfoProvider.notifyIfNoConnection(viewPager);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onChangeTranslation(long translationId) {
        viewPager.setCurrentItem(0);
        translationFragment.setCurrentTranslation(translationId);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //ignore
    }

    @Override
    public void onPageSelected(int position) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            IBinder windowToken = currentFocus.getWindowToken();
            if (windowToken != null)
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        }
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(position).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //ignore
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        int position;
        switch (id) {
            case R.id.translation:
                position = 0;
                break;
            case R.id.history:
                position = 1;
                break;
            case R.id.settings:
                position = 2;
                break;
            default:
                return false;
        }
        viewPager.setCurrentItem(position);
        return true;
    }
}
