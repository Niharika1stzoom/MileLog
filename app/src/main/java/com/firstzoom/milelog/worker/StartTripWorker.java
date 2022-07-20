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
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Tag;
import com.firstzoom.milelog.model.TempLocationList;
import com.firstzoom.milelog.model.Trip;
import com.firstzoom.milelog.repository.Repository;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.AppExecutors;
import com.firstzoom.milelog.util.AppUtil;
import com.firstzoom.milelog.util.LocationUtil;
import com.firstzoom.milelog.util.PermissionUtil;
import com.firstzoom.milelog.util.SharedPrefUtil;
import com.firstzoom.milelog.util.TrackerUtil;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


public class StartTripWorker extends Worker {
    Context mContext;

    public StartTripWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.isGPSEnabled(getApplicationContext());
            PermissionUtil.hasLocationActivityPermissions(getApplicationContext());
            return Result.failure();
        }
        Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(mContext)
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null);
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(Location location) {
                Trip trip = new Trip(null, null, location.getLatitude(), location.getLongitude(),
                        null, null, new Date(), null, null);
                AppExecutors.databaseWriteExecutor.execute(() -> {
                            Trip lastTrip = AppDatabase.getDatabase(mContext.getApplicationContext()).tripDao().getLastTrip();
                            if (lastTrip != null) {
                                long[] diff = AppUtil.getDifference(lastTrip.getEndTime(), new Date());
                                Integer minGap = SharedPrefUtil.getPreferenceMinGap(mContext);
                                if (minGap == 0)
                                    minGap = 1;
                                if (diff[0] <= minGap) {
                                    SharedPrefUtil.setLastId(mContext, lastTrip.getId());
                                    LocationUtil.startForegroundService(mContext, Math.toIntExact(lastTrip.getId()));
                                    long j = AppDatabase.getDatabase(mContext.getApplicationContext()).locationPathDao()
                                            .insert(new LocationPath(location.getLatitude(), location.getLongitude(), Math.toIntExact(lastTrip.getId())));
                                } else {
                                    long i = AppDatabase.getDatabase(mContext.getApplicationContext()).tripDao().insert(trip);
                                    SharedPrefUtil.setLastId(mContext.getApplicationContext(), Math.toIntExact(i));
                                    LocationUtil.startForegroundService(mContext, Math.toIntExact(i));
                                    long j = AppDatabase.getDatabase(mContext.getApplicationContext()).locationPathDao()
                                            .insert(new LocationPath(location.getLatitude(), location.getLongitude(), Math.toIntExact(i)));
                                }
                            }//insert
                            else {
                                long i = AppDatabase.getDatabase(mContext.getApplicationContext()).tripDao().insert(trip);
                                SharedPrefUtil.setLastId(mContext.getApplicationContext(), Math.toIntExact(i));
                                LocationUtil.startForegroundService(mContext, Math.toIntExact(i));
                                long j = AppDatabase.getDatabase(mContext.getApplicationContext()).locationPathDao()
                                        .insert(new LocationPath(location.getLatitude(), location.getLongitude(), Math.toIntExact(i)));
                            }
                        }
                );
            }
        }).addOnFailureListener(e -> Log.d(AppConstants.TAG, "Failed Location Start Trip" + e.getLocalizedMessage()));

        return ListenableWorker.Result.success();
    }
}
