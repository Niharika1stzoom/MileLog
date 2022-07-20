package com.firstzoom.milelog.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Tag;
import com.firstzoom.milelog.model.Trip;

@Database(entities = {Trip.class, Tag.class, LocationPath.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class,DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "trip_database";

    public abstract TripDao tripDao();
    public abstract LocationPathDao locationPathDao();

    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return sInstance;
    }
}