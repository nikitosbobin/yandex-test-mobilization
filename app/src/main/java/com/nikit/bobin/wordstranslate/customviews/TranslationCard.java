package com.nikit.bobin.wordstranslate.customviews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TranslationCard extends RelativeLayout {
    @BindView(R.id.lookup_list)
    ListView lookupList;
    @BindView(R.id.original_text_label)
    TextView originalTextView;
    @BindView(R.id.translated_text_label)
    TextView translatedTextView;
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

    private void init() {
        inflate(getContext(), R.layout.translation_card, this);
        if (isInEditMode()) return;

        // Views injection
        ButterKnife.bind(this);

        originalTextView.setMovementMethod(new ScrollingMovementMethod());
        translatedTextView.setMovementMethod(new ScrollingMovementMethod());

        adapter = new LookupListAdapter(getContext());
        lookupList.setAdapter(adapter);
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
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
}
