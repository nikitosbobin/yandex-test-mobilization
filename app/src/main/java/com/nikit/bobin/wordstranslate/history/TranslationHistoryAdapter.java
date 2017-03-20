package com.nikit.bobin.wordstranslate.history;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

public class TranslationHistoryAdapter extends BaseAdapter {
    private IStorage<TranslatedText> translatedTextIStorage;
    private LayoutInflater lInflater;
    private TranslatedText currentTranslation;
    private int lastCount;

    public TranslationHistoryAdapter(Context context, IStorage<TranslatedText> translatedTextIStorage) {
        this.translatedTextIStorage = translatedTextIStorage;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        int count = translatedTextIStorage.getCount();
        if (currentTranslation != null)
            count++;
        return count;
    }

    @Override
    public TranslatedText getItem(int position) {
        if (currentTranslation == null)
            return translatedTextIStorage.getSavedItemsReversed()[position];
        if (position == 0)
            return currentTranslation;
        return translatedTextIStorage.getSavedItemsReversed()[position - 1];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.success_translation_layout, parent, false);
        }
        TranslatedText item = getItem(position);
        TextView original = (TextView) view.findViewById(R.id.original_text_label);
        TextView translated = (TextView) view.findViewById(R.id.translated_text_label);
        TextView direction = (TextView) view.findViewById(R.id.direction_label);

        original.setText(item.getTranslation().getOriginalText());
        translated.setText(item.getTranslatedText());
        direction.setText(item.getTranslation().getDirection().toString().toUpperCase());
        if (firstItemIsTemporary() && position == 0) {
            view.setOnLongClickListener(null);
        }
        if (currentTranslation != null && position == 0 && lastCount != getCount()) {
            YoYo.with(Techniques.FadeInLeft)
                    .duration(400)
                    .playOn(view);
            lastCount = getCount();
        }
        return view;
    }

    public void setCurrentTranslatedText(@Nullable TranslatedText translatedText) {
        Ensure.inUiThread();

        currentTranslation = translatedText;
    }

    public boolean firstItemIsTemporary() {
        return currentTranslation != null;
    }
}
