package com.nikit.bobin.wordstranslate.customviews;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.AnimationsFactory;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;

import javax.inject.Inject;

import at.markushi.ui.CircleButton;

/*Custom toggle that provides the ability to conveniently
*   change images for different switch states from markup.
*   Works same as floating action button, but unlike fab this view has nice click animation
*/
public class FloatingActionToggle extends CircleButton implements View.OnClickListener {
    @Inject
    AnimationsFactory animationsFactory;

    private boolean isChecked;
    private Drawable activeImage;
    private Drawable passiveImage;
    private FloatingActionToggle.OnCheckedChangeListener onCheckedChangeListener;
    private boolean animatingShow;
    private boolean animatingHide;
    private YoYo.AnimationComposer hideAnimation;
    private YoYo.AnimationComposer showAnimation;

    public FloatingActionToggle(Context context) {
        super(context);
    }

    public FloatingActionToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionToggle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Dependency injection
        IocSetup.getComponent().inject(this);

        initAnimations();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomToggle,
                0, 0);
        try {
            activeImage = a.getDrawable(R.styleable.CustomToggle_activeImage);
            passiveImage = a.getDrawable(R.styleable.CustomToggle_passiveImage);
        } finally {
            a.recycle();
        }
        setImageDrawable(passiveImage);
        setOnClickListener(this);
    }

    private void initAnimations() {
        hideAnimation = animationsFactory
                .createSlideOutRightAnimation(300)
                .onStart(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        animatingHide = true;
                        animatingShow = false;
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        setVisibility(GONE);
                        animatingHide = false;
                    }
                });

        showAnimation = animationsFactory
                .createSlideInRightAnimation(300)
                .onStart(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        setVisibility(VISIBLE);
                        animatingShow = true;
                        animatingHide = false;
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        animatingShow = false;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        isChecked = !isChecked;
        setImageDrawable(isChecked ? activeImage : passiveImage);
        if (onCheckedChangeListener != null)
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        setImageDrawable(isChecked ? activeImage : passiveImage);
    }

    public void hide() {
        if (getVisibility() == GONE || animatingHide)
            return;
        hideAnimation.playOn(this);
    }

    public void show() {
        if (getVisibility() == VISIBLE || animatingShow)
            return;
        showAnimation.playOn(this);
    }

    public void setOnCheckedChangeListener(
            FloatingActionToggle.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(FloatingActionToggle toggleView, boolean isChecked);
    }
}
