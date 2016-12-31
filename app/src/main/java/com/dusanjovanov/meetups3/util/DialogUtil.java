package com.dusanjovanov.meetups3.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.dusanjovanov.meetups3.R;

/**
 * Created by duca on 31/12/2016.
 */

public class DialogUtil {

    public static void showNoConnectionDialog(Context context){
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setTitle("No connection")
                .setMessage("Request failed. Please connect to network and try again.")
                .show();
    }
}
