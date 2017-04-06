package com.nikit.bobin.wordstranslate.net;

import android.content.Context;
import android.net.ConnectivityManager;

import com.nikit.bobin.wordstranslate.core.Ensure;

public class NetworkConnectionInfoProvider {
    private ConnectivityManager connectivityManager;

    public NetworkConnectionInfoProvider(Context context) {
        Ensure.notNull(context, "context");

        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnectedToInternet() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
