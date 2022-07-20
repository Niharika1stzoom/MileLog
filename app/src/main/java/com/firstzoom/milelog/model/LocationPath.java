package com.firstzoom.milelog.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Trip.class,
                parentColumns = "id",
                childColumns = "trip_id",
                onDelete = CASCADE
        )})
public class LocationPath {
    @PrimaryKey(autoGenerate = true)
    int id;
    Double latitude, longitude;

    public LocationPath(Double latitude, Double longitude, int trip_id) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.trip_id = trip_id;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    int trip_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}


