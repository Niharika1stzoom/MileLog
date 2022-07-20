package com.firstzoom.milelog.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.firstzoom.milelog.model.LocationPath;

import androidx.annotation.RequiresApi;

import com.firstzoom.milelog.MainActivity;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.worker.UserInVehicleService;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackerUtil {


    @SuppressLint("MissingPermission")
   public static void stopTracking(Context context){
        Task<Location> locationTask= LocationServices.getFusedLocationProviderClient(context.getApplicationContext())
               .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null);
       locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
           @RequiresApi(api = Build.VERSION_CODES.N)
           @Override
           public void onSuccess(Location location) {
               //TODO: save the trip details in database
               int lastId=SharedPrefUtil.getLastId(context.getApplicationContext());
               if(lastId==0)
                   return;
               LocationUtil.stopForegroundService(context.getApplicationContext());

               AppExecutors.databaseWriteExecutor.execute(() ->{
                   Double distance=0.0;
                   long j=AppDatabase.getDatabase(context.getApplicationContext()).locationPathDao()
                           .insert(new LocationPath(location.getLatitude(), location.getLongitude(),SharedPrefUtil.getLastId(context) ));

                   List<LocationPath> paths=AppDatabase.getDatabase(context.getApplicationContext()).
                           locationPathDao().findLocationPaths(Math.toIntExact(lastId));
                   if(paths!=null) {
                       distance=DistanceUtil.calDistance(paths);
                       Log.d(AppConstants.TAG,"Paths got"+distance);
                   }
                   Log.d(AppConstants.TAG,"Paths saved"+distance);

                   AppDatabase.getDatabase(context.getApplicationContext()).tripDao().updateEndTrip(location.getLatitude(),
                           location.getLongitude(),new Date(),distance,SharedPrefUtil.getLastId(context.getApplicationContext()));
                     SharedPrefUtil.removeLastID(context);

               });
           }
       }).addOnFailureListener(e -> Log.d("MileDebug", "Failed"+e.getLocalizedMessage() ));

    }


    public static void startForegroundService(MainActivity mainActivity, Class<UserInVehicleService> service) {
        Intent intent = new Intent(mainActivity,service );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainActivity.getApplicationContext().startForegroundService(intent);
        }
    }

    public static void stopForegroundService(MainActivity mainActivity, Class<UserInVehicleService> service) {
        Intent intent = new Intent(mainActivity,service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainActivity.getApplicationContext().stopService(intent);
        }
    }

    public static ActivityTransitionRequest getActivityTransitionRequest(){
        ActivityTransitionRequest request = new ActivityTransitionRequest(getActivityTransitionList());
        return request;
    }
    public static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";

            default:
                return "UNKNOWN";
        }

    }

    private static List<ActivityTransition> getActivityTransitionList() {
       List<ActivityTransition> activityTransitionList=new ArrayList<>();
       activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(AppConstants.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(AppConstants.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
       /* activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

*/
        return activityTransitionList;
    }

}
