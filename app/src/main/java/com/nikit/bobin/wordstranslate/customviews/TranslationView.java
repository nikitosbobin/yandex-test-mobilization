package com.nikit.bobin.wordstranslate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;


public class TranslationView extends RelativeLayout
        implements CompoundButton.OnCheckedChangeListener {
    private OnFavoriteChangeListener onFavoriteChangeListener;
    private int position;

    public TranslationView(Context context) {
        super(context);
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTranslatedText(int position, TranslatedText translatedText) {
        Ensure.notNull(translatedText, "translatedText");

        inflate(getContext(), R.layout.success_translation_layout, this);

        this.position = position;

        TextView original = (TextView) findViewById(R.id.original_text_label);
        TextView translated = (TextView) findViewById(R.id.translated_text_label);
        TextView direction = (TextView) findViewById(R.id.direction_label);
        ToggleButton starButton = (ToggleButton) findViewById(R.id.star_button);

        starButton.setChecked(translatedText.isFavorite());
        starButton.setOnCheckedChangeListener(this);

        original.setText(translatedText.getTranslation().getOriginalText());
        translated.setText(translatedText.getTranslatedText());
        direction.setText(translatedText.getTranslation().getDirection().toString());
    }

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener onFavoriteChangeListener) {
        this.onFavoriteChangeListener = onFavoriteChangeListener;
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        if (v.getId() == R.id.star_button && onFavoriteChangeListener != null) {
            onFavoriteChangeListener.onFavoriteChange(position, isChecked);
        }
    }

    public interface OnFavoriteChangeListener {
        void onFavoriteChange(int position, boolean value);
    }
}
