package com.sportsCommittee.sportsinventory;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectivityCheck {


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



}
