package com.firstzoom.milelog.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firstzoom.milelog.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtil {
    public static final String[] permissions={
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    public static boolean checkPermissions(Context context) {

        if(EasyPermissions.hasPermissions(context,permissions))
            return true;
        else{
            askPermissions(context);
        }
        return false;
    }
    public static Boolean hasAudioPermissions(Context context) {
         String[] permissions={
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACTIVITY_RECOGNITION,
        };
        if (hasPermissionAudio(context) && hasPermissionRead(context) && hasPermissionWrite(context))
        {
            return true;
        }
        else
        {
         Log.d(AppConstants.TAG,"Asking perm");

            EasyPermissions.requestPermissions(
                    (Activity)context,
                    context.getString(R.string.rationale_permission),
                    151,
                    permissions);
            return false;
        }
    }

    public static Boolean hasReadWritePermissions(Context context) {
        String[] permissions={
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (hasPermissionRead(context) && hasPermissionWrite(context))
        {
            return true;
        }
        else
        {
            EasyPermissions.requestPermissions(
                    (Activity)context,
                    context.getString(R.string.rationale_permission),
                    152,
                    permissions);
            return false;
        }
    }
    public static Boolean hasLocationActivityPermissions(Context context) {
        String[] permissions={
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION

        };
        if (hasPermissionLocation(context) && hasPermissionActivity(context))
        {
            return true;
        }
        else
        {
            EasyPermissions.requestPermissions(
                    (Activity)context,
                    context.getString(R.string.rationale_permission),
                    153,
                    permissions);
            return false;
        }
    }
    public static Boolean hasLocationBackgroundPermissions(Context context) {
        String[] permissions={
                Manifest.permission.ACCESS_BACKGROUND_LOCATION

        };
        if (hasPermissionLocationBackground(context) )
        {
            return true;
        }
        else
        {
            EasyPermissions.requestPermissions(
                    (Activity)context,
                    context.getString(R.string.rationale_permission_background),
                    154,
                    permissions);
            return false;
        }
    }

    private static boolean hasPermissionWrite(Context context) {
        return EasyPermissions.hasPermissions(context,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static boolean hasPermissionRead(Context context) {
        return EasyPermissions.hasPermissions(context,Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private static Boolean hasPermissionAudio(Context context){
        return EasyPermissions.hasPermissions(context,Manifest.permission.RECORD_AUDIO);

    }
    private static Boolean hasPermissionActivity(Context context){
        return EasyPermissions.hasPermissions(context,Manifest.permission.ACTIVITY_RECOGNITION);

    }

   private static Boolean hasPermissionLocation(Context context){
        return EasyPermissions.hasPermissions(context,Manifest.permission.ACCESS_FINE_LOCATION);

    }
    private static Boolean hasPermissionLocationBackground(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    }
    private static void askPermissions(Context context) {
        Log.d(AppConstants.TAG,"In ask");
        EasyPermissions.requestPermissions(
                (Activity) context,
                context.getString(R.string.rationale_permission),
                25,
                permissions);
    }



    public static  boolean isGPSEnabled(Context context) {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }
}
