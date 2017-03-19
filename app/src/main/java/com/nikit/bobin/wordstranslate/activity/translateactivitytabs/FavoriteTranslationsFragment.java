package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.history.IStorage;
import com.nikit.bobin.wordstranslate.history.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class FavoriteTranslationsFragment extends Fragment {
    ListView historyListView;
    @Inject
    IStorage<TranslatedText> translatedTextStorage;

    public FavoriteTranslationsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        App.getComponent().injectFavoriteTranslationsFragment(this);
        //ButterKnife.bind(this, view);
        //registerForContextMenu(historyListView);
        return view;
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
}
