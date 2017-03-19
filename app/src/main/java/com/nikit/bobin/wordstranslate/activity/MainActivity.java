package com.nikit.bobin.wordstranslate.activity;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.Tab2Fragment;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslateActivityPagerAdapter;
import com.nikit.bobin.wordstranslate.activity.translateactivitytabs.TranslationFragment;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends AppCompatActivity implements MaterialTabListener {
    @BindView(R.id.materialTabHost) MaterialTabHost tabHost;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.lang_selector) LanguageSelectorView selectorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PagerAdapter pagerAdapter = new TranslateActivityPagerAdapter(
                getSupportFragmentManager(),
                new String[]{"Translate", "Favourite"},
                new Fragment[]{new TranslationFragment().setSelectorView(selectorView), new Tab2Fragment()});

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                int visibility = selectorView.getVisibility();
                if (visibility == View.VISIBLE) {
                    YoYo.with(Techniques.SlideOutLeft)
                            .duration(100)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    selectorView.setVisibility(View.GONE);
                                }
                            })
                            .playOn(selectorView);
                } else {
                    YoYo.with(Techniques.SlideInLeft)
                            .duration(100)
                            .playOn(selectorView);
                    selectorView.setVisibility(View.VISIBLE);
                }
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
