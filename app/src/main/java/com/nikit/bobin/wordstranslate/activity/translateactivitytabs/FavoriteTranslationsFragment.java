package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

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
import android.widget.ListView;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.adapters.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.customviews.CustomToggle;
import com.nikit.bobin.wordstranslate.functional.CurrentTranslationChangeListener;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.storage.AbstractDatabaseOneTableContext;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteTranslationsFragment extends Fragment
        implements AdapterView.OnItemClickListener,
        CustomToggle.OnCheckedChangeListener, AbstractDatabaseOneTableContext.OnItemsUpdateListener {
    @BindView(R.id.favorite_list)
    ListView favoriteListView;
    @BindView(R.id.favorite_button)
    CustomToggle favoriteButton;
    @Inject
    ITranslationsDatabase translationsDatabase;
    @Inject
    ILog log;
    @BindView(R.id.favorite_fragment_title)
    TextView title;
    private TranslationHistoryAdapter adapter;
    private Handler uiHandler;
    private CurrentTranslationChangeListener onCurrentTranslationChangeListener;

    public FavoriteTranslationsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        App.getComponent().injectFavoriteTranslationsFragment(this);
        ButterKnife.bind(this, view);

        uiHandler = new Handler(getContext().getMainLooper());
        adapter = new TranslationHistoryAdapter(getContext(), translationsDatabase);

        favoriteButton.setOnCheckedChangeListener(this);
        translationsDatabase.setOnItemsUpdateListener(this);

        favoriteListView.setAdapter(adapter);
        favoriteListView.setOnItemClickListener(this);
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
    public void onDatabaseChange() {
        if (uiHandler != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    favoriteListView.invalidateViews();
                }
            });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onCurrentTranslationChangeListener != null)
            onCurrentTranslationChangeListener.onChangeTranslation(id);
    }

    public FavoriteTranslationsFragment setOnCurrentTranslationChangeListener(
            CurrentTranslationChangeListener onCurrentTranslationChangeListener) {
        this.onCurrentTranslationChangeListener = onCurrentTranslationChangeListener;
        return this;
    }

    @Override
    public void onCheckedChanged(CustomToggle toggleView, boolean isChecked) {
        if (toggleView.getId() == R.id.favorite_button) {
            title.setText(isChecked ? R.string.favorite : R.string.history);
            adapter.setFavoriteFilteringState(isChecked);
            favoriteListView.invalidateViews();
        }
    }
}
