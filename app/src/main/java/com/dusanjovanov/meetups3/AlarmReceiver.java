package com.dusanjovanov.meetups3;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.NotificationCompat;

import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by duca on 31/12/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "AlrmRecv";
    private int messageCount;
    private int newMessageCount;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final User currentUser = (User) intent.getSerializableExtra(ConstantsUtil.EXTRA_CURRENT_USER);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        messageCount = preferences.getInt("message_count",0);
        newMessageCount = preferences.getInt("new_message_count",0);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("chat").child(String.valueOf(currentUser.getId())).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int firebaseMessageCount = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                ChatMessage message = snapshot1.getValue(ChatMessage.class);
                                if(!message.getDisplayName().equals(currentUser.getDisplayName())){
                                    firebaseMessageCount++;
                                }
                            }
                            firebaseMessageCount--;
                        }

                        if(firebaseMessageCount> messageCount){
                            newMessageCount += firebaseMessageCount-messageCount;
                            preferences.edit()
                                    .putInt("message_count",messageCount+newMessageCount)
                                    .putInt("new_message_count",newMessageCount)
                                    .apply();
                        }

                        Intent notifIntent = new Intent(context,MainScreenActivity.class);
                        notifIntent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
                        notifIntent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notifIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                        if(newMessageCount>0){
                            Notification notification = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_event_yellow_18dp)
                                    .setContentTitle("Nove poruke")
                                    .setContentText("Imate "+newMessageCount+" novih poruka")
                                    .setContentIntent(pendingIntent)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setColor(ResourcesCompat.getColor(context.getResources(),R.color.primary,null))
                                    .build();

                            NotificationManager notificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int idNotification = 001;

                            notificationManager.notify(idNotification,notification);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }


}
