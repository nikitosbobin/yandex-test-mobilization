package com.nikit.bobin.wordstranslate;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class AnimationsFactory {
    public YoYo.AnimationComposer createFadeInAnimation(long duration) {
        return YoYo.with(Techniques.FadeIn).duration(duration);
    }

    public YoYo.AnimationComposer createRotateAnimation(long duration) {
        return YoYo.with(new RotateAnimator()).duration(duration);
    }

    public YoYo.AnimationComposer createSlideOutDownAnimation(long duration) {
        return YoYo.with(Techniques.SlideOutDown).duration(duration);
    }

    public YoYo.AnimationComposer createSlideInUpAnimation(long duration) {
        return YoYo.with(Techniques.SlideInUp).duration(duration);
    }

    public YoYo.AnimationComposer createSlideInRightAnimation(long duration) {
        return YoYo.with(Techniques.SlideInRight).duration(duration);
    }

    public YoYo.AnimationComposer createSlideOutRightAnimation(long duration) {
        return YoYo.with(Techniques.SlideOutRight).duration(duration);
    }

    private class RotateAnimator extends BaseViewAnimator {
        protected void prepare(View target) {
            getAnimatorAgent().playTogether(
                    ObjectAnimator.ofFloat(target, "rotation", -180, 0)
            );
        }
    }
}
