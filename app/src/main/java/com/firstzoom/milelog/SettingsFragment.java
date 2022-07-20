package com.firstzoom.milelog;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.firstzoom.milelog.util.PermissionUtil;
import com.firstzoom.milelog.util.TrackerUtil;
import com.firstzoom.milelog.worker.UserInVehicleService;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        findPreference(getString(R.string.key_track)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                boolean isSwitch = (boolean) newValue;
                if(isSwitch) {
                    if(PermissionUtil.hasLocationActivityPermissions(getContext()))
                        if(PermissionUtil.hasLocationBackgroundPermissions(getContext())) {
                            startService();
                        }
                }
                else {
                    stopService();
                }

                return true;
            }
        });
    }
    private void stopService() {
        if(!isMyServiceRunning(UserInVehicleService.class))
            return;
        TrackerUtil.stopForegroundService((MainActivity) getActivity(), UserInVehicleService.class);
    }

    private void startService() {
        if(PermissionUtil.isGPSEnabled(getContext()))
        if(PermissionUtil.hasLocationActivityPermissions(getContext()))
            if(PermissionUtil.hasLocationBackgroundPermissions(getContext())) {
                if (isMyServiceRunning(UserInVehicleService.class))
                    stopService();
                TrackerUtil.startForegroundService((MainActivity) getActivity(), UserInVehicleService.class);
            }
        else {
                //TODO:Manage switch off
            }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}