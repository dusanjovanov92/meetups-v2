package com.dusanjovanov.meetups3;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dusanjovanov.meetups3.util.NetworkUtil;

/**
 * Created by duca on 31/12/2016.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private ConnectionChangeListener connectionChangeListener;

    public interface ConnectionChangeListener{
        void onDisconnected();
        void onConnected();
    }

    public NetworkReceiver(ConnectionChangeListener connectionChangeListener) {
        this.connectionChangeListener = connectionChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(!NetworkUtil.isOnline(context)){
            connectionChangeListener.onDisconnected();
        }
        else{
            connectionChangeListener.onConnected();
        }


    }


}
