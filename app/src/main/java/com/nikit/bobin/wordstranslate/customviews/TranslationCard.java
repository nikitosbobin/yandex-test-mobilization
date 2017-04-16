package com.nikit.bobin.wordstranslate.customviews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.adapters.LookupListAdapter;
import com.nikit.bobin.wordstranslate.helpers.Ensure;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*Custom view that which is located under the input field on the first page of the app.
*   Displays current state of translation and translation lookup
*/
public class TranslationCard extends RelativeLayout {
    @BindView(R.id.lookup_list)
    ListView lookupList;
    @BindView(R.id.original_text_label)
    TextView originalTextView;
    @BindView(R.id.translated_text_label)
    TextView translatedTextView;
    @BindView(R.id.yandex_translator_label)
    TextView yandexTranslatorLabel;
    @BindView(R.id.yandex_dictionary_label)
    TextView yandexDictionaryLabel;
    private ClipboardManager clipboard;
    private TranslatedText translatedText;
    private LookupListAdapter adapter;
    private WordLookup currentLookup;

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

        yandexTranslatorLabel.setText(Html.fromHtml(yandexTranslatorLabel.getText().toString()));
        yandexTranslatorLabel.setMovementMethod(LinkMovementMethod.getInstance());

        yandexDictionaryLabel.setText(Html.fromHtml(yandexDictionaryLabel.getText().toString()));
        yandexDictionaryLabel.setMovementMethod(LinkMovementMethod.getInstance());

        originalTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        translatedTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

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

        currentLookup = lookup;
        if (lookupList.isShown()) {
            int visibility = lookup.isEmpty() ? GONE : VISIBLE;
            lookupList.setVisibility(visibility);
            yandexDictionaryLabel.setVisibility(visibility);
        } else {
            lookupList.setVisibility(INVISIBLE);
            yandexDictionaryLabel.setVisibility(INVISIBLE);
        }

        adapter.setNewLookup(lookup);
        lookupList.invalidateViews();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (h < oldh) {
            lookupList.setVisibility(INVISIBLE);
            yandexDictionaryLabel.setVisibility(INVISIBLE);
        } else {
            int visibility = currentLookup == null || currentLookup.isEmpty()
                    ? GONE
                    : VISIBLE;
            lookupList.setVisibility(visibility);
            yandexDictionaryLabel.setVisibility(visibility);
        }
    }
}
