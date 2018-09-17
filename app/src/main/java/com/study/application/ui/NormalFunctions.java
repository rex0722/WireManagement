package com.study.application.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NormalFunctions {


    public static boolean isNetworkConnected(NetworkInfo ni){
        return ni != null && ni.isAvailable() && ni.isConnected();
    }

}
