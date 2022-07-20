package com.firstzoom.milelog.util;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Trip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ImportExportUtil {
    public static JSONObject getJsonTrip(List<Trip> list,JSONObject mObject) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Trip>>() {
        }.getType();
        String stdJson = gson.toJson(list, type);

        try {
            mObject.put(AppConstants.TRIP_TABLE, stdJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mObject;
    }

    public static JSONObject getJsonPath(List<LocationPath> list,JSONObject mObject) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<LocationPath>>() {
        }.getType();
        String stdJson = gson.toJson(list, type);

        try {
            mObject.put(AppConstants.LOCATION_PATH_TABLE, stdJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mObject;
    }
}
