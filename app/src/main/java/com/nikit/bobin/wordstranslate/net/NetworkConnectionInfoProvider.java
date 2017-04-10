package com.nikit.bobin.wordstranslate.net;

import android.content.Context;
import android.net.ConnectivityManager;

import com.nikit.bobin.wordstranslate.helpers.Ensure;

//Simple component than response for detecting internet availability
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
