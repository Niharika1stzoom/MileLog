package com.firstzoom.milelog.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.firstzoom.milelog.MainActivity;
import com.firstzoom.milelog.worker.LocationService;
import com.firstzoom.milelog.worker.UserInVehicleService;
import com.google.android.gms.location.LocationRequest;

public class LocationUtil {
    public static LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
       // locationRequest.setFastestInterval(20000);
       // locationRequest.setNumUpdates(1);
        return locationRequest;
    }
    public static void startForegroundService(Context context,int id) {
        Intent intent = new Intent(context,LocationService.class);
        intent.putExtra(AppConstants.KEY_TRIP_ID,id);
        if(isMyServiceRunning(LocationService.class,context))
            stopForegroundService(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
    }

    public static void stopForegroundService(Context context) {
        if(!isMyServiceRunning(LocationService.class,context))
            return;
        Intent intent = new Intent(context,LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.stopService(intent);
        }
    }
    public static boolean  isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
