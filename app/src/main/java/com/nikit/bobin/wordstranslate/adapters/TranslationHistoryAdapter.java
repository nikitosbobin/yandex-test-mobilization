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
import com.nikit.bobin.wordstranslate.customviews.TranslationView;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

public class TranslationHistoryAdapter extends BaseAdapter
        implements TranslationView.OnFavoriteChangeListener {
    private Context context;
    private final ITranslationsDatabase translationsDatabase;
    private boolean needFavoriteFiltering;

    public TranslationHistoryAdapter(Context context, ITranslationsDatabase translationsDatabase) {
        this.context = context;
        this.translationsDatabase = translationsDatabase;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TranslationView translationView = new TranslationView(context);
        TranslatedText item = getItem(position);
        translationView.setTranslatedText(position, item);
        translationView.setOnFavoriteChangeListener(this);
        return translationView;
    }

    public void setFavoriteFilteringState(boolean state) {
        this.needFavoriteFiltering = state;
    }

    @Override
    public void onFavoriteChange(int position, boolean value) {
        TranslatedText currentTranslation = getItem(position).changeFavoriteState(value);
        translationsDatabase.addOrUpdate(currentTranslation);
    }
}
