package com.nikit.bobin.wordstranslate.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.activity.fragments.FavoriteTranslationsFragment;
import com.nikit.bobin.wordstranslate.activity.fragments.SettingsFragment;
import com.nikit.bobin.wordstranslate.activity.fragments.TranslationFragment;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;
import com.nikit.bobin.wordstranslate.translating.ITranslator;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

//Main activity that manage all fragments in app
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
    private Fragment[] fragments;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    //Entry point on activity created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dependency and views injection
        ButterKnife.bind(this);
        IocSetup.getComponent().inject(this);

        // fragments initializing
        fragments = new Fragment[]{
                new TranslationFragment(),
                new FavoriteTranslationsFragment().setOnCurrentTranslationChangeListener(this),
                new SettingsFragment()
        };
        translationFragment = (TranslationFragment) fragments[0];

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            public Fragment getItem(int position) {
                return fragments[position];
            }

            public int getCount() {
                return fragments.length;
            }
        };

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        notifyIfNoConnection();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    /*This event called when user click on any translation item in listView.
    *   Notify fragment manager when need to change current translation on TranslationFragment
    */
    public void onChangeTranslation(long translationId) {
        viewPager.setCurrentItem(0);
        translationFragment.setCurrentTranslation(translationId);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //ignore
    }

    @Override
    //Fragment selected event handler
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
    //Navigation bottom bar tab selected event handler
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

    //Notify user when internet not available
    private void notifyIfNoConnection() {
        if (!networkConnectionInfoProvider.isConnectedToInternet())
            Snackbar.make(viewPager, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
    }
}
