package com.example.uonliaquatarain.hangoutapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.android.gms.common.api.Response;

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

    public void notifyThis(final List<String> sender_information, String method) {
        String message = "";
        Intent intent = new Intent(context, UserProfile.class);
        if(method == Constatnts.FRIEND_REQUEST) {
            message = sender_information.get(1) + " has sent you Friend Request!";
            intent.putExtra("sender_name", sender_information.get(1));
            intent.putExtra("sender_username", sender_information.get(2));
            intent.putExtra("sender_pic_url", sender_information.get(3));
            intent.putExtra(Constatnts.FRIEND_REQUEST, Constatnts.FRIEND_REQUEST);
        }
        else if(method == Constatnts.FRIEND_REQUEST_ACCEPTED){
            message = sender_information.get(1) + " has accepted you Friend Request!";
//            intent.putExtra("sender_name", sender_information.get(1));
//            intent.putExtra("sender_username", sender_information.get(2));
//            intent.putExtra("sender_pic_url", sender_information.get(3));
//            intent.putExtra(Constatnts.FRIEND_REQUEST, Constatnts.FRIEND_REQUEST);
        }
        else if(method == Constatnts.MESSAGE){
            intent = new Intent(context,ChatUI.class);
            message = sender_information.get(1) + " has sent you a message!";
            intent.putExtra("username", sender_information.get(2));
            intent.putExtra(Constatnts.MESSAGE, Constatnts.MESSAGE);
        }
        else if(method == Constatnts.EVENT_REQUEST_RECEIVED){
            intent = new Intent(context,EventRequests.class);
            message = sender_information.get(1) + " is asking you for " + sender_information.get(5) + " on " +
                    sender_information.get(6) + " at " + sender_information.get(7) + " at " + sender_information.get(8) ;
            intent.putExtra("event_message", message);
            intent.putExtra("latlng", sender_information.get(9));
            intent.putExtra("event_name", sender_information.get(5));
        }
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.custom_notification_message, message);

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
