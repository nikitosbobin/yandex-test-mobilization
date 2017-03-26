package com.nikit.bobin.wordstranslate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.nikit.bobin.wordstranslate.net.NetworkConnectionInfoProvider;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NetworkConnectionInfoProvider_Tests {
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private NetworkConnectionInfoProvider provider;

    @Before
    public void setUp() {
        context = mock(Context.class);
        connectivityManager = mock(ConnectivityManager.class);
        networkInfo = mock(NetworkInfo.class);

        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);

        provider = new NetworkConnectionInfoProvider(context);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_if_context_is_null() {
        new NetworkConnectionInfoProvider(null);
    }

    @Test
    public void should_init_ConnectivityManager() {
        verify(context).getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Test
    public void isConnectedToInternet_should_correctly_determine_internet_connection() {
        boolean connectedToInternet = provider.isConnectedToInternet();

        assertEquals(true, connectedToInternet);
    }

    @Test
    public void isConnectedToInternet_should_correctly_determine_no_internet_connection() {
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(null);

        boolean connectedToInternet = provider.isConnectedToInternet();

        assertEquals(false, connectedToInternet);
    }
}
