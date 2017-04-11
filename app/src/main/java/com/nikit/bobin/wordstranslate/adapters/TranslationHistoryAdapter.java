package com.nikit.bobin.wordstranslate.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nikit.bobin.wordstranslate.customviews.TranslationView;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.storage.OnTranslationDatabaseUpdateListener;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

//Custom adapter for translation history listView in second fragment of app
public class TranslationHistoryAdapter extends BaseAdapter
        implements TranslationView.OnFavoriteChangeListener, OnTranslationDatabaseUpdateListener {
    private Context context;
    private final ITranslationsDatabase translationsDatabase;
    private boolean needFavoriteFiltering;
    private TranslatedText[] favoriteTranslations;
    private TranslatedText[] allTranslations;

    public TranslationHistoryAdapter(Context context, ITranslationsDatabase translationsDatabase) {
        this.context = context;
        this.translationsDatabase = translationsDatabase;
        translationsDatabase.addOnItemsUpdateListener(this);
        onTranslationDatabaseChange();
    }

    @Override
    public int getCount() {
        if (needFavoriteFiltering) {
            return favoriteTranslations.length;
        } else {
            return allTranslations.length;
        }
    }

    @Override
    public TranslatedText getItem(int position) {
        if (needFavoriteFiltering) {
            return favoriteTranslations[position];
        } else {
            return allTranslations[position];
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TranslationView translationView;
        if (convertView == null) {
            translationView = new TranslationView(context);
            translationView.setOnFavoriteChangeListener(this);
        } else {
            translationView = (TranslationView) convertView;
        }
        TranslatedText item = getItem(position);
        translationView.setTranslatedText(position, item);
        return translationView;
    }

    @Override
    public void onFavoriteChange(int position, boolean value) {
        TranslatedText currentTranslation = getItem(position);
        currentTranslation.setFavorite(value);
        translationsDatabase.save(currentTranslation);
    }

    @Override
    public void onTranslationDatabaseChange() {
        favoriteTranslations = translationsDatabase.getFavoriteTranslations(true);
        allTranslations = translationsDatabase.getAllTranslations(true);
    }

    public void setFavoriteFilteringState(boolean state) {
        this.needFavoriteFiltering = state;
    }
}
