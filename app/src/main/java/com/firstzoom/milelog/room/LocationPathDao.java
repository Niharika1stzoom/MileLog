package com.firstzoom.milelog.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.firstzoom.milelog.model.LocationPath;

import java.util.List;

@Dao
public interface LocationPathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(LocationPath locationPath);

    @Update
    void update(LocationPath... locationPaths);

    @Delete
    void delete(LocationPath... locationPaths);

    @Query("SELECT * FROM locationpath")
    LiveData<List<LocationPath>> getAllLocationPaths();

    @Query("SELECT * FROM locationpath WHERE trip_id=:tripId")
    List<LocationPath> findLocationPaths(final int tripId);
}