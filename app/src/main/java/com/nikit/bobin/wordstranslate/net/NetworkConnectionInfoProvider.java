package com.nikit.bobin.wordstranslate.net;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkConnectionInfoProvider {
    private ConnectivityManager connectivityManager;

    public NetworkConnectionInfoProvider(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnected() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
