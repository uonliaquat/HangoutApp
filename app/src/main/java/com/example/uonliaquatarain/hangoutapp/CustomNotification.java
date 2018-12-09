package com.example.uonliaquatarain.hangoutapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.content.Context.NOTIFICATION_SERVICE;

public class CustomNotification  {

    private final String CHANNEL_ID  = "personal_notifications";
    private final int NOTIFICATION_ID = 001;

    private Context context;

    public CustomNotification(Context context) {
        this.context = context;
        createNotificationChannel();

    }


   public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "personal_Notification";
            String description = "Friend Request";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notifyThis(List<String> sender_information, String method) {
        String message = "";
        if(method == Constatnts.FRIEND_REQUEST) {
            message = sender_information.get(0) + " has sent you Friend Request!";
        }
        else if(method == Constatnts.FRIEND_REQUEST_ACCEPTED){
            message = sender_information.get(0) + " has accepted you Friend Request!";
        }
        Intent intent = new Intent(context, UserProfile.class);
        intent.putExtra("sender_name", sender_information.get(0));
        intent.putExtra("sender_username", sender_information.get(1));
        intent.putExtra("sender_pic_url", sender_information.get(2));
        intent.putExtra(Constatnts.FRIEND_REQUEST, Constatnts.FRIEND_REQUEST);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.custom_notification_image,R.drawable.defaultpic_icon);
        remoteViews.setTextViewText(R.id.custom_notification_message, message);

//        Intent i = new Intent(context, MainActivity.class) ;
//        intent.putExtra(Constatnts.FRIEND_REQUEST, "accepted");
//        intent.putExtra("sender_name", sender_information.get(0));
//        intent.putExtra("sender_username", sender_information.get(1));
//        intent.putExtra("sender_pic_url", sender_information.get(2));
//        PendingIntent pi = PendingIntent.getActivity(context, 0 , i, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.accept_btn, pi);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.friends_icon)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setContent(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, mBuilder.build());
    }


}
