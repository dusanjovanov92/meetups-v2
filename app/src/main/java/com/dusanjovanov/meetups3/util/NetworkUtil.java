package com.dusanjovanov.meetups3.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by duca on 31/12/2016.
 */

public class NetworkUtil {

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void makeToast(Context context){
        Toast.makeText(context, "No network", Toast.LENGTH_SHORT).show();
    }

    public static void makeSnackbar(CoordinatorLayout layout){
        Snackbar.make(layout,"No network",Snackbar.LENGTH_INDEFINITE).show();
    }

    public static void registerNetworkReceiver(){
        
    }
}
