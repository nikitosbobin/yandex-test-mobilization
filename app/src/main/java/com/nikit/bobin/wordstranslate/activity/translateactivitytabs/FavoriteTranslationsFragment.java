package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.history.IStorage;
import com.nikit.bobin.wordstranslate.history.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteTranslationsFragment extends ToolBarControlFragment implements View.OnClickListener {
    @BindView(R.id.favorite_list)
    ListView favoriteListView;
    @BindView(R.id.favorite_button)
    ImageView favoriteButton;
    @Inject
    IStorage<TranslatedText> translatedTextStorage;
    @BindView(R.id.favorite_fragment_title)
    TextView title;
    private TranslationHistoryAdapter adapter;
    private Drawable allListImage;
    private Drawable favoriteListImage;
    private boolean favoriteFiltered;

    public FavoriteTranslationsFragment() {
        super();
    }

    @Override
    public ToolBarControlFragment setContext(Context context) {
        allListImage = context.getResources().getDrawable(R.drawable.all_list);
        favoriteListImage = context.getResources().getDrawable(R.drawable.favorite_list);
        return super.setContext(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        App.getComponent().injectFavoriteTranslationsFragment(this);
        ButterKnife.bind(this, view);

        favoriteButton.setOnClickListener(this);
        /*translatedTextStorage.setOnItemsUpdateListener(new Runnable() {
            @Override
            public void run() {
                uiThreadExecuter.post(new Runnable() {
                    @Override
                    public void run() {
                        //favoriteListView.invalidateViews();
                    }
                });
            }
        });*/
        //registerForContextMenu(historyListView);
        init();
        return view;
    }

    private void init() {
        adapter = new TranslationHistoryAdapter(getContext(), translatedTextStorage);
        //favoriteListView.setAdapter(adapter);
    }

    @Override
    CharSequence getTitle() {
        return "Favorite";
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.favorite_button) {
            if (favoriteFiltered) {
                favoriteButton.setImageDrawable(allListImage);
                favoriteFiltered = false;
                title.setText("History");
            } else {
                favoriteButton.setImageDrawable(favoriteListImage);
                favoriteFiltered = true;
                title.setText("Favorite");
            }
        }
    }
}
