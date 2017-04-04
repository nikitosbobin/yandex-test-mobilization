package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;


public class TranslationView extends RelativeLayout
        implements CustomToggle.OnCheckedChangeListener {
    private OnFavoriteChangeListener onFavoriteChangeListener;
    private int position;
    private TextView original;
    private TextView translated;
    private TextView direction;
    private CustomToggle favoriteButton;

    public TranslationView(Context context) {
        super(context);
        init();
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.success_translation_layout, this);
        original = (TextView) findViewById(R.id.original_text_label);
        translated = (TextView) findViewById(R.id.translated_text_label);
        direction = (TextView) findViewById(R.id.direction_label);
        favoriteButton = (CustomToggle) findViewById(R.id.star_button);
        favoriteButton.setOnCheckedChangeListener(this);
    }

    public void setTranslatedText(int position, TranslatedText translatedText) {
        Ensure.notNull(translatedText, "translatedText");

        this.position = position;

        favoriteButton.setChecked(translatedText.isFavorite());

        original.setText(translatedText.getTranslation().getOriginalText());
        translated.setText(translatedText.getTranslatedText());
        direction.setText(translatedText.getTranslation().getDirection().toString());
    }

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener onFavoriteChangeListener) {
        this.onFavoriteChangeListener = onFavoriteChangeListener;
    }

    @Override
    public void onCheckedChanged(CustomToggle toggleView, boolean isChecked) {
        if (onFavoriteChangeListener != null && toggleView.getId() == R.id.star_button) {
            onFavoriteChangeListener.onFavoriteChange(position, isChecked);
        }
    }

    public interface OnFavoriteChangeListener {
        void onFavoriteChange(int position, boolean value);
    }
}
