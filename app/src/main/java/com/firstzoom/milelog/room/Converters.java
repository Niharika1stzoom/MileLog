package com.firstzoom.milelog.room;

import androidx.room.TypeConverter;

import com.firstzoom.milelog.model.Tag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<Tag> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Tag>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }



    @TypeConverter
    public static String fromArrayList(ArrayList<Tag> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}