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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.adapters.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.customviews.CircularCustomToggle;
import com.nikit.bobin.wordstranslate.ioc.IocSetup;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.storage.AbstractDatabaseOneTableContext;
import com.nikit.bobin.wordstranslate.storage.ITranslationsDatabase;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteTranslationsFragment extends Fragment
        implements AdapterView.OnItemClickListener,
        CircularCustomToggle.OnCheckedChangeListener,
        AbstractDatabaseOneTableContext.OnItemsUpdateListener,
        AbsListView.OnScrollListener {
    @BindView(R.id.favorite_list)
    ListView favoriteListView;
    @BindView(R.id.favorite_button)
    CircularCustomToggle circleButton;
    @BindView(R.id.favorite_fragment_title)
    TextView title;
    @BindDrawable(R.drawable.all_list)
    Drawable allList;
    @BindDrawable(R.drawable.favorite_list)
    Drawable favoriteList;
    @Inject
    ITranslationsDatabase translationsDatabase;
    @Inject
    ILog log;
    private TranslationHistoryAdapter adapter;
    @Inject
    Handler uiHandler;
    private CurrentTranslationChangeListener onCurrentTranslationChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        // Dependency and views injection
        IocSetup.getComponent().injectFavoriteTranslationsFragment(this);
        ButterKnife.bind(this, view);

        adapter = new TranslationHistoryAdapter(getContext(), translationsDatabase);

        circleButton.setOnCheckedChangeListener(this);
        translationsDatabase.addOnItemsUpdateListener(this);

        favoriteListView.setAdapter(adapter);
        favoriteListView.setOnItemClickListener(this);
        favoriteListView.setOnScrollListener(this);
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
    public void onCheckedChanged(CircularCustomToggle toggleView, boolean isChecked) {
        if (toggleView.getId() == R.id.favorite_button) {
            title.setText(isChecked ? R.string.favorite : R.string.history);
            adapter.setFavoriteFilteringState(isChecked);
            adapter.notifyDataSetChanged();
            favoriteListView.invalidateViews();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && view.canScrollList(-1))
            circleButton.hide();
        else
            circleButton.show();
    }

    public interface CurrentTranslationChangeListener {
        void onChangeTranslation(long translationId);
    }
}
