package com.nikit.bobin.wordstranslate.customviews;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.AnimationsFactory;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;

import javax.inject.Inject;

import at.markushi.ui.CircleButton;

public class CircularCustomToggle extends CircleButton implements View.OnClickListener {
    @Inject
    AnimationsFactory animationsFactory;
    private boolean isChecked;
    private Drawable activeImage;
    private Drawable passiveImage;
    private CircularCustomToggle.OnCheckedChangeListener onCheckedChangeListener;
    private boolean animating;
    private YoYo.AnimationComposer hideAnimation;
    private YoYo.AnimationComposer showAnimation;

    public CircularCustomToggle(Context context) {
        super(context);
    }

    public CircularCustomToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularCustomToggle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        IocSetup.getComponent().injectCircularCustomToggle(this);

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
                .createSlideOutRightAnimation(500)
                .onStart(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        animating = true;
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        setVisibility(GONE);
                        animating = false;
                    }
                });
        showAnimation = animationsFactory
                .createSlideInRightAnimation(500)
                .onStart(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        setVisibility(VISIBLE);
                        animating = true;
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    public void call(Animator animator) {
                        animating = false;
                    }
                });
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        setImageDrawable(isChecked ? activeImage : passiveImage);
    }

    @Override
    public void onClick(View v) {
        isChecked = !isChecked;
        setImageDrawable(isChecked ? activeImage : passiveImage);
        if (onCheckedChangeListener != null)
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
    }

    public void hide() {
        if (getVisibility() == GONE || animating)
            return;
        hideAnimation.playOn(this);
    }

    public void show() {
        if (getVisibility() == VISIBLE || animating)
            return;
        showAnimation.playOn(this);
    }

    public void setOnCheckedChangeListener(
            CircularCustomToggle.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CircularCustomToggle toggleView, boolean isChecked);
    }
}