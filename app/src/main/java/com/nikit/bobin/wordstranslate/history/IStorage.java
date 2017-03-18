package com.nikit.bobin.wordstranslate.history;

public interface IStorage<TItem> {
    TItem[] getSavedItems();
    boolean saveItem(TItem item);
    int getCount();
}
