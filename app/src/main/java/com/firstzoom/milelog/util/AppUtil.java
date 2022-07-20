package com.firstzoom.milelog.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppUtil {
    public static String getDisplayDate(Date date) {
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL HH:mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd HH:mm");
        String dateTime = simpleDateFormat.format(date);
        return dateTime;
    }
    public static void showSnackbar(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    public static long[] getDifference(Date startTime, Date endTime) {
        long[] time= new long[2];
        long diff = endTime.getTime() - startTime.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long mins = diff / 60;
        time[0]=minutes;
        time[1]=seconds;
        return time;
    }
}
