package com.firstzoom.milelog.worker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.receiver.ActivityTransitionReceiver;
import com.firstzoom.milelog.repository.Repository;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.AppExecutors;
import com.firstzoom.milelog.util.LocationUtil;
import com.firstzoom.milelog.util.NotificationUtil;
import com.firstzoom.milelog.util.SharedPrefUtil;
import com.firstzoom.milelog.util.TrackerUtil;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LocationService extends Service {
    private IBinder mBinder = new LocationService.LocalBinder();
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation().hasAccuracy())
                repository.insertLocationPath(id, locationResult.getLastLocation());
            Log.d("MileDebug", "Lat" + locationResult.getLastLocation().getLatitude() + locationResult.getLastLocation().hasAccuracy());
        }
    };
    @Inject
    Repository repository;
    int id;

    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
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
        id = intent.getIntExtra(AppConstants.KEY_TRIP_ID, 0);
        if (id != 0)
            trackAndAddLocation();
        return START_STICKY;
    }

    void showForeground() {
        Notification notification = NotificationUtil.createForegroundNotification(this);
        startForeground(AppConstants.FOREGROUND_NOTIFICATION_ID, notification);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        showForeground();

    }

    private void trackAndAddLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(LocationUtil.getLocationRequest(), locationCallback, Looper.getMainLooper());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
      removeLocationUpdates();
    }

    private void removeLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }
}