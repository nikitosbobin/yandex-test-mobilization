package com.nikit.bobin.wordstranslate.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

public class TranslationHistoryAdapter extends BaseAdapter {
    private Context context;
    private IStorage<TranslatedText> translatedTextIStorage;
    private LayoutInflater lInflater;

    public TranslationHistoryAdapter(Context context, IStorage<TranslatedText> translatedTextIStorage) {
        this.context = context;
        this.translatedTextIStorage = translatedTextIStorage;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return translatedTextIStorage.getCount();
    }

    @Override
    public TranslatedText getItem(int position) {
        return translatedTextIStorage.getSavedItems()[position];
    }

    @Override
    public long getItemId(int position) {
        return translatedTextIStorage.getSavedItems()[position].getId();
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

        return view;
    }
}
