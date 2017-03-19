package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.customviews.LanguageSelectorView;
import com.nikit.bobin.wordstranslate.history.IStorage;
import com.nikit.bobin.wordstranslate.history.TranslationHistoryAdapter;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneCallback;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationFragment extends Fragment implements TextWatcher, View.OnClickListener {
    @BindView(R.id.history_list)
    ListView historyList;
    @BindView(R.id.editText)
    EditText input;
    @BindView(R.id.clear_button)
    ImageView clearButton;
    @Inject
    ITranslator translator;
    @Inject
    IStorage<TranslatedText> historyStorage;
    @Inject
    ILog log;
    private Handler uiThreadHandler;
    private LanguageSelectorView selectorView;
    private Context context;
    private TranslatedText currentTranslation;
    private TranslationHistoryAdapter adapter;
    private Date lastTextEditingDate;

    public TranslationFragment() {
        super();
    }

    public TranslationFragment setSelectorView(LanguageSelectorView selectorView) {
        this.selectorView = selectorView;
        selectorView.setOnSwapListener(new Runnable() {
            @Override
            public void run() {
                Editable text = input.getText();
                onTextChanged(text, 0, 0, text.length());
            }
        });
        return this;
    }

    public TranslationFragment setContext(Context context) {
        this.context = context;
        uiThreadHandler = new Handler(context.getMainLooper());
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        App.getComponent().injectTranslationFragment(this);
        ButterKnife.bind(this, view);

        adapter = new TranslationHistoryAdapter(getContext(), historyStorage);
        historyList.setAdapter(adapter);
        input.addTextChangedListener(this);
        clearButton.setOnClickListener(this);
        registerForContextMenu(historyList);
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //ignore
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        log.info("s: %s start: %d before: %d count: %d", s, start, before, count);
        Date now = new Date();
        if (lastTextEditingDate != null && now.getTime() - lastTextEditingDate.getTime() > 10000) {
            commitTranslation();
        }
        lastTextEditingDate = now;
        if (s.length() == 0) {
            clearButton.setVisibility(View.GONE);
            adapter.setCurrentTranslatedText(null);
            historyList.invalidateViews();
            return;
        }
        clearButton.setVisibility(View.VISIBLE);
        translator
                .translateAsync(new Translation(s.toString(), selectorView.getDirection()))
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(final TranslatedText result) {
                        if (!result.isSuccess()) return;
                        currentTranslation = result;
                        uiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setCurrentTranslatedText(result);
                                historyList.invalidateViews();
                            }
                        });
                    }
                });
    }

    @Override
    public void afterTextChanged(Editable s) {
        //ignore
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_button) {
            commitTranslation();
            input.setText("");
            clearButton.setVisibility(View.GONE);
        }
    }

    private void commitTranslation() {
        TranslatedText[] items = historyStorage.getSavedItemsReversed();
        if (items.length == 0 || (items.length > 0 && !items[0].equals(currentTranslation))) {
            historyStorage.saveOrUpdateItem(currentTranslation);
            adapter.setCurrentTranslatedText(null);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.FIRST) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;
            TranslatedText[] savedItems = historyStorage.getSavedItemsReversed();
            if (savedItems.length < position + 1) {
                adapter.setCurrentTranslatedText(null);
                historyList.invalidateViews();
                return true;
            }
            TranslatedText toDelete = historyStorage.getSavedItemsReversed()[position];
            if (historyStorage.delete(toDelete)) {
                historyList.invalidateViews();
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }
}
