package com.nikit.bobin.wordstranslate.activity.translateactivitytabs;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;

public abstract class ToolBarControlFragment extends Fragment {
    private Handler uiHandler;

    public ToolBarControlFragment setContext(Context context) {
        uiHandler = new Handler(context.getMainLooper());
        return this;
    }

    protected void runOnUiThread(Runnable runnable) {
        if (uiHandler != null)
            uiHandler.post(runnable);
    }

    abstract CharSequence getTitle();
}
