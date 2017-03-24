package com.nikit.bobin.wordstranslate.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

public class TranslationHistoryAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private final ITranslationsDatabase translationsDatabase;
    private boolean needFavoriteFiltering;
    private Drawable activeStar;
    private Drawable disabledStar;

    public TranslationHistoryAdapter(Context context, ITranslationsDatabase translationsDatabase) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.translationsDatabase = translationsDatabase;
        activeStar = context.getResources().getDrawable(R.drawable.favorite_active);
        disabledStar = context.getResources().getDrawable(R.drawable.favorite_disable);
    }

    @Override
    public int getCount() {
        if (needFavoriteFiltering) {
            return translationsDatabase.getFavoriteTranslationsCount();
        } else {
            return translationsDatabase.getAllTranslationsCount();
        }
    }

    @Override
    public TranslatedText getItem(int position) {
        if (needFavoriteFiltering) {
            return translationsDatabase.getFavoriteTranslations(true)[position];
        } else {
            return translationsDatabase.getAllTranslations(true)[position];
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    // todo: create custom view for remove closures
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.success_translation_layout, parent, false);
        }
        TranslatedText item = getItem(position);
        TextView original = (TextView) view.findViewById(R.id.original_text_label);
        TextView translated = (TextView) view.findViewById(R.id.translated_text_label);
        TextView direction = (TextView) view.findViewById(R.id.direction_label);
        ImageView starButton = (ImageView) view.findViewById(R.id.star_button);
        Log.d("current position", position+"");
        starButton.setImageDrawable(item.isFavorite() ? activeStar : disabledStar);
        original.setText(item.getTranslation().getOriginalText());
        translated.setText(item.getTranslatedText());
        direction.setText(item.getTranslation().getDirection().toString().toUpperCase());
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslatedText currentTranslation = getItem(position);
                boolean favorite = currentTranslation.isFavorite();
                currentTranslation.setIsFavorite(!favorite);
                ((ImageView) view).setImageDrawable(currentTranslation.isFavorite() ? activeStar : disabledStar);
                translationsDatabase.addOrUpdate(currentTranslation);
            }
        });

        return view;
    }

    public void setFavoriteFilteringState(boolean state) {
        this.needFavoriteFiltering = state;
    }
}
