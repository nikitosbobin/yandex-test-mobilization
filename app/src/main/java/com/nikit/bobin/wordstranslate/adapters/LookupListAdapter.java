package com.nikit.bobin.wordstranslate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.translating.models.SynonymGroup;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

public class LookupListAdapter extends BaseAdapter {
    private WordLookup lookup;
    private LayoutInflater inflater;

    public LookupListAdapter(Context context) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lookup == null || lookup.isEmpty() ? 0 : lookup.getSynonyms().length;
    }

    @Override
    public SynonymGroup getItem(int position) {
        return lookup.getSynonyms()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.lookup_synonym_layout, parent, false);
        }
        SynonymGroup synonymGroup = getItem(position);

        TextView synonymView = (TextView) view.findViewById(R.id.lookup_text);
        TextView meanView = (TextView) view.findViewById(R.id.lookup_mean);

        synonymView.setText(Strings.join(synonymGroup.getSynonyms(), ", "));
        if (synonymGroup.getMean().length == 0) {
            meanView.setVisibility(View.GONE);
        } else {
            meanView.setVisibility(View.VISIBLE);
            meanView.setText(String.format("(%s)", Strings.join(synonymGroup.getMean(), ", ")));
        }
        return view;
    }

    public void setNewLookup(WordLookup lookup) {
        this.lookup = lookup;
    }
}
