package com.firstzoom.milelog.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.firstzoom.milelog.R;
import com.firstzoom.milelog.model.TempLocationList;

import java.util.Date;
import java.util.List;

public class SharedPrefUtil {
    synchronized public static void setLastId(Context context, int id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(AppConstants.KEY_LAST_ID,id);
        editor.apply();
    }
    public static int getLastId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(AppConstants.KEY_LAST_ID, 0);
    }

    public static void removeLastID(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(AppConstants.KEY_LAST_ID);
        editor.apply();
    }

    public static Integer getPreferenceMinGap(Context context){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String combine = sharedPreferences.getString(context.getString(R.string.key_combine), "0");
        return Integer.parseInt(combine);
    }
}
