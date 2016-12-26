package com.dusanjovanov.meetups3.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.dusanjovanov.meetups3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by duca on 21/12/2016.
 */

public class FirebaseUtil {

    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void showGooglePlayErrorDialog(Context context){
        new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(R.string.google_play_error)
                .setIcon(R.drawable.ic_error_black_24dp)
                .show();
    }
}
