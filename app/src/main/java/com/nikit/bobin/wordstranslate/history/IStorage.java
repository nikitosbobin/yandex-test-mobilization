package com.nikit.bobin.wordstranslate.history;

public interface IStorage<TItem> {
    TItem[] getSavedItems();
    TItem[] getSavedItemsReversed();
    boolean saveOrUpdateItem(TItem item);
    int getCount();
    boolean delete(TItem item);
    void setOnItemsUpdateListener(Runnable onItemsUpdateListener);
}
