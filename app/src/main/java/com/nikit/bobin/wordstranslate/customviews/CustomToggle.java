package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.nikit.bobin.wordstranslate.R;

public class CustomToggle extends android.support.v7.widget.AppCompatImageView
        implements View.OnClickListener {
    private Drawable activeImage;
    private Drawable passiveImage;
    private boolean isChecked;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CustomToggle(Context context) {
        super(context);
    }

    public CustomToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
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

    @Override
    public void onClick(View view) {
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

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    interface OnCheckedChangeListener {
        void onCheckedChanged(CustomToggle toggleView, boolean isChecked);
    }
}
