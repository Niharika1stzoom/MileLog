package com.firstzoom.milelog.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.firstzoom.milelog.model.Tag;
import com.firstzoom.milelog.model.Trip;

import java.util.Date;
import java.util.List;
@Dao
public interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertList(List<Trip> Trip);

    @Query("DELETE from trip_table")
    void deleteAll();

    @Delete
    void deleteTrip(Trip Trip);

    @Query("UPDATE trip_table SET latitudeStop=:latitude,longitudeStop=:longitude,endTime=:stopTime,distance=:dist WHERE id = :id")
    void updateEndTrip(Double latitude,Double longitude,@TypeConverters(DateConverter.class) Date stopTime, Double dist,int id);


    @Query("Select * from trip_table where startTime BETWEEN :start AND :end")
    LiveData<List<Trip>> getTripList(@TypeConverters(DateConverter.class) Date start,@TypeConverters(DateConverter.class) Date end);

    @Query("Select * from trip_table ")
    LiveData<List<Trip>> getTripList();

    @Query("Select * from trip_table order by id DESC LIMIT 1 ")
    Trip getLastTrip();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Trip Trip);

    //Tags
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertTagList(List<Tag> Tag);

    @Query("DELETE from tag_table")
    void deleteAllTags();

    @Delete
    void deleteTag(Tag Tag);

    @Query("Select * from tag_table")
    LiveData<List<Tag>> getTagList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Tag Tag);

    @Query("Select * from trip_table where id=:tripId")
    LiveData<Trip> getTrip(int tripId);

}
