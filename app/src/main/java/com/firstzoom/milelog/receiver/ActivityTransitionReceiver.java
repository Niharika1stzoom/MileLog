package com.firstzoom.milelog.receiver;

import static com.firstzoom.milelog.util.AppConstants.TRANSITIONS_RECEIVER_ACTION;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.firstzoom.milelog.MainActivity;
import com.firstzoom.milelog.R;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.LocationUtil;
import com.firstzoom.milelog.util.NotificationUtil;
import com.firstzoom.milelog.util.SharedPrefUtil;
import com.firstzoom.milelog.util.TrackerUtil;
import com.firstzoom.milelog.worker.EndTripWorker;
import com.firstzoom.milelog.worker.StartTripWorker;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.material.math.MathUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ActivityTransitionReceiver extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (!TextUtils.equals(TRANSITIONS_RECEIVER_ACTION, intent.getAction())) {
            Log.d(AppConstants.TAG, "Received an unsupported action in TransitionsReceiver: action = " +
                    intent.getAction());
            return;
        }
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            if (result != null && result.getProbableActivities().size() > 0)
                for (DetectedActivity event : result.getProbableActivities()) {
                    String info = "Transition: " + TrackerUtil.toActivityString(event.getType()) + " " +
                            event.getConfidence() + "  " +
                            new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
                    if (event.getType() == AppConstants.IN_VEHICLE && event.getConfidence() >= AppConstants.START_TRIP_CONFIDENCE && SharedPrefUtil.getLastId(context) == 0) {
                        OneTimeWorkRequest trackWorkRequest =
                                new OneTimeWorkRequest.Builder(StartTripWorker.class)
                                        .build();
                        WorkManager.getInstance(context).enqueue(trackWorkRequest);
                        Intent activityIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                        NotificationUtil.showNotification(context, context.getString(R.string.trip_started),
                                context.getString(R.string.trip_start_desc) + new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()), activityIntent, 2);
                        break;
                    } else if (event.getType() != AppConstants.IN_VEHICLE && event.getType() != DetectedActivity.TILTING
                            && SharedPrefUtil.getLastId(mContext) != 0 && event.getConfidence() > AppConstants.END_TRIP_CONFIDENCE) {
                        OneTimeWorkRequest trackWorkRequest =
                                new OneTimeWorkRequest.Builder(EndTripWorker.class)
                                        .build();
                        WorkManager.getInstance(context).enqueue(trackWorkRequest);
                        Intent activityIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                        NotificationUtil.showNotification(context, context.getString(R.string.trip_stopped) , context.getString(R.string.trip_finish) + new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()), activityIntent, 2);
                        break;
                    }
                }
        }
    }
}