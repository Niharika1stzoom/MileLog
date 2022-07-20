package com.firstzoom.milelog.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.firstzoom.milelog.R;

public class NotificationUtil {

   public static Notification createForegroundNotification(Context context) {
        NotificationChannel channel = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(AppConstants.FOREGROUND_CHANNEL_ID,
                    AppConstants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    context, AppConstants.FOREGROUND_CHANNEL_ID);
            notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.foreground_title))
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setChannelId(AppConstants.FOREGROUND_CHANNEL_ID)
                    .build();
        }
        return notification;
    }
  /*  public static void sendNotification(Context context, String title, String message, Intent intent) {
        //SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
        int requestID = (int) System.currentTimeMillis()+4;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String CHANNEL_ID = "worker";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "worker";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(requestID, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + requestID);
    }*/
  public static void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
      //SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
      int requestID = (int) System.currentTimeMillis();
      PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      String CHANNEL_ID = "Trip";// The id of the channel.
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, AppConstants.FOREGROUND_CHANNEL_ID)
              .setSmallIcon(R.mipmap.ic_launcher)
              .setContentTitle(title)
              .setContentText(message)
              .setAutoCancel(true)
              .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
              .setContentIntent(pendingIntent);
      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          CharSequence name = "Trip";// The user-visible name of the channel.
          int importance = NotificationManager.IMPORTANCE_HIGH;
          NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
          notificationManager.createNotificationChannel(mChannel);
      }
      notificationManager.notify(reqCode, notificationBuilder.build());
  }
}