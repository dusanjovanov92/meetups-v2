package com.dusanjovanov.meetups3.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.dusanjovanov.meetups3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static void deleteMeetingNode(String firebaseNode){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("meetings").child(firebaseNode).removeValue();
    }

    public static void deleteChatNodes(int idUser1,int idUser2,String firebaseNode){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("chat").child(String.valueOf(idUser1)).child(firebaseNode).removeValue();
        dbRef.child("chat").child(String.valueOf(idUser2)).child(firebaseNode).removeValue();
    }
}
