package com.nikit.bobin.wordstranslate.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.nikit.bobin.wordstranslate.R;

// refactored
public class NetworkConnectionInfoProvider {
    private ConnectivityManager connectivityManager;

    public NetworkConnectionInfoProvider(Context context) {
        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnectedToInternet() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public void notifyIfNoConnection(View view) {
        if (!isConnectedToInternet())
            Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
    }
}
