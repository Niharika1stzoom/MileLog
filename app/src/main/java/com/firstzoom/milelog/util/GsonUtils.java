package com.firstzoom.milelog.util;


import com.firstzoom.milelog.model.TempLocationList;
import com.google.gson.Gson;

import java.util.List;

public class GsonUtils {


    public static String getGsonObject(TempLocationList obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
    public static TempLocationList getModelObjectUser(String loc) {
        Gson gson = new Gson();
        return gson.fromJson(loc, TempLocationList.class);

    }

    public static String getGsonObjectList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public static List<String> getModelObjectList(String savedSet) {
        Gson gson = new Gson();
        return gson.fromJson(savedSet, List.class);
    }
}
