package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.functional.OnItemsUpdateListener;
import com.nikit.bobin.wordstranslate.history.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.adapters.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteTranslationsFragment extends Fragment
        implements View.OnClickListener, OnItemsUpdateListener {
    @BindView(R.id.favorite_list)
    ListView favoriteListView;
    @BindView(R.id.favorite_button)
    ImageView favoriteButton;
    @Inject
    ITranslationsDatabase translationsDatabase;
    @BindView(R.id.favorite_fragment_title)
    TextView title;
    private TranslationHistoryAdapter adapter;
    private Drawable allListImage;
    private Drawable favoriteListImage;
    private boolean favoriteFiltered;
    private Handler uiHandler;

    public FavoriteTranslationsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        App.getComponent().injectFavoriteTranslationsFragment(this);
        ButterKnife.bind(this, view);

        if (!translationsDatabase.isConnected())
            translationsDatabase.connect(getContext());

        uiHandler = new Handler(getContext().getMainLooper());
        adapter = new TranslationHistoryAdapter(getContext(), translationsDatabase);
        allListImage = getResources().getDrawable(R.drawable.all_list);
        favoriteListImage = getResources().getDrawable(R.drawable.favorite_list);

        favoriteButton.setOnClickListener(this);
        translationsDatabase.setOnItemsUpdateListener(this);

        favoriteListView.setAdapter(adapter);
        registerForContextMenu(favoriteListView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TranslatedText text = adapter.getItem(info.position);
        if (translationsDatabase.delete(text)) {
            favoriteListView.invalidateViews();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.favorite_button) {
            if (favoriteFiltered) {
                favoriteButton.setImageDrawable(allListImage);
                favoriteFiltered = false;
                title.setText(R.string.history);
                adapter.setFavoriteFilteringState(false);
                favoriteListView.invalidateViews();
            } else {
                favoriteButton.setImageDrawable(favoriteListImage);
                favoriteFiltered = true;
                title.setText(R.string.favorite);
                adapter.setFavoriteFilteringState(true);
                favoriteListView.invalidateViews();
            }
        }
    }

    @Override
    public void onDatabaseChange() {
        if (uiHandler != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    favoriteListView.invalidateViews();
                }
            });
    }
}
