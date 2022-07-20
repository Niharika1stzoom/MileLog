package com.firstzoom.milelog.worker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.WorkManager;

import com.firstzoom.milelog.receiver.ActivityTransitionReceiver;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.LocationUtil;
import com.firstzoom.milelog.util.NotificationUtil;
import com.firstzoom.milelog.util.PermissionUtil;
import com.firstzoom.milelog.util.TrackerUtil;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class UserInVehicleService extends Service {
    private IBinder mBinder = new UserInVehicleService.LocalBinder();
    private List<ActivityTransition> transitions;
    private ActivityTransitionRequest request;
    private PendingIntent mPendingIntent;
    private ActivityTransitionReceiver receiver;

    public class LocalBinder extends Binder {
        public UserInVehicleService getServerInstance() {
            return UserInVehicleService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    void startService() {
        Notification notification = NotificationUtil.createForegroundNotification(this);
        startForeground(AppConstants.FOREGROUND_NOTIFICATION_ID, notification);
    }

    PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, ActivityTransitionReceiver.class);
        intent.setAction(AppConstants.TRANSITIONS_RECEIVER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    void registerReceiver() {
        receiver = new ActivityTransitionReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver,
                new IntentFilter(AppConstants.TRANSITIONS_RECEIVER_ACTION));
    }

    void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
        mPendingIntent = getPendingIntent();
        request = TrackerUtil.getActivityTransitionRequest();
        registerReceiver();
        requestActivityUpdates();
    }


    public void requestActivityUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.hasLocationActivityPermissions(getApplicationContext());
            return;
        }
        Task<Void> task = ActivityRecognition.getClient(this)
                .requestActivityUpdates(AppConstants.activityDetectionInterval, mPendingIntent);//10 seconds
        //.requestActivityTransitionUpdates(request, mPendingIntent);
        task.addOnSuccessListener(
                result -> {
                    Log.d(AppConstants.TAG, "Listening for activities");
                }
        );
        task.addOnFailureListener(
                e -> {
                    // Handle error
                    Log.d(AppConstants.TAG, "User Activity listener error" + e.getLocalizedMessage());
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdates();
    }


    private void removeActivityUpdates() {
        unRegisterReceiver();
        LocationUtil.stopForegroundService(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.hasLocationActivityPermissions(getApplicationContext());
            return;
        }
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mPendingIntent)
                .addOnSuccessListener(aVoid -> {
                    mPendingIntent.cancel();
                })
                .addOnFailureListener(e -> Log.d(AppConstants.TAG, "Transitions could not be unregistered: " + e));
    }
}