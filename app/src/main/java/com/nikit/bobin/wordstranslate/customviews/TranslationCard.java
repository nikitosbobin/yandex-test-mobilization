package com.nikit.bobin.wordstranslate.customviews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.adapters.LookupListAdapter;
import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TranslationCard extends RelativeLayout {
    private ListView lookupList;
    private TextView originalTextView;
    private TextView translatedTextView;
    private ClipboardManager clipboard;
    private TranslatedText translatedText;

    private LookupListAdapter adapter;

    public TranslationCard(Context context) {
        super(context);
        init();
    }

    public TranslationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TranslationCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @OnClick(R.id.copy_translation_button)
    public void copyTranslationToClipboard() {
        if (translatedText != null) {
            ClipData clip = ClipData.newPlainText(
                    translatedText.getTranslation().getDirection().toString(),
                    translatedText.getTranslatedText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.translation_copied, Toast.LENGTH_SHORT).show();
        }
    }

    public void fillTranslation(TranslatedText translatedText) {
        Ensure.inUiThread();
        Ensure.notNull(translatedText, "translatedText");

        this.translatedText = translatedText;
        originalTextView.setText(translatedText.getTranslation().getOriginalText());
        translatedTextView.setText(translatedText.getTranslatedText());
    }

    public void setLookup(WordLookup lookup) {
        Ensure.inUiThread();

        lookupList.setVisibility(lookup.isEmpty() ? GONE : VISIBLE);
        adapter.setNewLookup(lookup);
        lookupList.invalidateViews();
    }

    private void init() {
        inflate(getContext(), R.layout.translation_card, this);
        originalTextView = (TextView) findViewById(R.id.original_text_label);
        translatedTextView = (TextView) findViewById(R.id.translated_text_label);
        lookupList = (ListView) findViewById(R.id.lookup_list);
        adapter = new LookupListAdapter(getContext());
        lookupList.setAdapter(adapter);
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ButterKnife.bind(this);
    }
}
