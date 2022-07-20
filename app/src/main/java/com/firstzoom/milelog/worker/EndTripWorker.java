package com.firstzoom.milelog.worker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.AppExecutors;
import com.firstzoom.milelog.util.DistanceUtil;
import com.firstzoom.milelog.util.LocationUtil;
import com.firstzoom.milelog.util.PermissionUtil;
import com.firstzoom.milelog.util.SharedPrefUtil;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.List;

public class EndTripWorker extends Worker {
    Context context;

    public EndTripWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.isGPSEnabled(getApplicationContext());
            PermissionUtil.hasLocationActivityPermissions(getApplicationContext());
            return Result.failure();
        }
        Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(context.getApplicationContext())
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null);
        locationTask.addOnSuccessListener(location -> {
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
                }
                AppDatabase.getDatabase(context.getApplicationContext()).tripDao().updateEndTrip(location.getLatitude(),
                        location.getLongitude(),new Date(),distance,SharedPrefUtil.getLastId(context.getApplicationContext()));
                SharedPrefUtil.removeLastID(context);
            });
        }).addOnFailureListener(e -> Log.d(AppConstants.TAG, "Failed Location End Trip"+e.getLocalizedMessage() ));
        return Result.success();
    }
}
