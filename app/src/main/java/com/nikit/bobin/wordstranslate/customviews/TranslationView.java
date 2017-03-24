package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;


public class TranslationView extends RelativeLayout
        implements  View.OnClickListener {
    private OnFavoriteChangeListener onFavoriteChangeListener;
    private int position;
    private Drawable activeStar;
    private Drawable disabledStar;
    private boolean state;
    private ImageView starButton;
    private TranslatedText translatedText;

    public TranslationView(Context context) {
        super(context);
        init(context);
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        activeStar = context.getResources().getDrawable(R.drawable.favorite_active);
        disabledStar = context.getResources().getDrawable(R.drawable.favorite_disable);
    }

    public void setTranslatedText(int position, TranslatedText translatedText) {
        Ensure.notNull(translatedText, "translatedText");

        this.translatedText = translatedText;
        inflate(getContext(), R.layout.success_translation_layout, this);
        this.position = position;
        TextView original = (TextView) findViewById(R.id.original_text_label);
        TextView translated = (TextView) findViewById(R.id.translated_text_label);
        TextView direction = (TextView) findViewById(R.id.direction_label);
        starButton = (ImageView) findViewById(R.id.star_button);

        original.setText(translatedText.getTranslation().getOriginalText());
        translated.setText(translatedText.getTranslatedText());
        direction.setText(translatedText.getTranslation().getDirection().toString());
        setFavoriteState(translatedText.isFavorite());

        starButton.setOnClickListener(this);
    }

    private void setFavoriteState(boolean value) {
        state = value;
        starButton.setImageDrawable(value ? activeStar : disabledStar);
    }

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener onFavoriteChangeListener) {
        this.onFavoriteChangeListener = onFavoriteChangeListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.star_button && onFavoriteChangeListener != null) {
            setFavoriteState(!state);
            onFavoriteChangeListener.onFavoriteChange(position, state);
        }
    }

    public interface OnFavoriteChangeListener {
        void onFavoriteChange(int position, boolean value);
    }
}
