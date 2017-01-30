package com.dusanjovanov.meetups3.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.NotificationCompat;

import com.dusanjovanov.meetups3.MeetingActivity;
import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by duca on 30/1/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "FirMesServ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String,String> data = remoteMessage.getData();

        Group group = new Group();
        group.setId(Integer.parseInt(data.get("group_id")));
        group.setName(data.get("group_name"));
        User admin = new User();
        admin.setId(Integer.parseInt(data.get("group_admin")));
        group.setAdmin(admin);

        Meeting meeting = new Meeting();
        meeting.setId(Integer.parseInt(data.get("meeting_id")));
        meeting.setLabel(data.get("meeting_label"));
        meeting.setFirebaseNode(data.get("meeting_firebase_node"));
        meeting.setStartTime(Long.parseLong(data.get("meeting_start_time")));

        User currentUser = new User();
        currentUser.setId(Integer.parseInt(data.get("user_id")));
        currentUser.setDisplayName(data.get("user_display_name"));
        currentUser.setEmail(data.get("user_email"));
        currentUser.setPhotoUrl(data.get("user_photo_url"));

        Intent intent = new Intent(this, MeetingActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
        intent.putExtra(ConstantsUtil.EXTRA_GROUP,group);
        intent.putExtra(ConstantsUtil.EXTRA_MEETING,meeting);
        intent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_event_yellow_18dp)
                .setContentTitle("Novi sastanak")
                .setContentText(group.getName()+" novi sastanak - "+meeting.getLabel())
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(ResourcesCompat.getColor(getResources(),R.color.primary,null))
                .build();

        int notificationId = 001;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId,notification);
    }
}
