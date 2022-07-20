package com.firstzoom.milelog.model;

import android.text.Editable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.firstzoom.milelog.room.Converters;
import com.firstzoom.milelog.room.DateConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "trip_table")
public class Trip implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id;
    @Ignore
    List<LocationPath> paths;

    //Location startLocation,stopLocation;
    String description,audioFilePath;
    Double latitudeStart;
    Double longitudeStart;
    Double latitudeStop;
    Double longitudeStop;
    Double distance;

    @TypeConverters(DateConverter.class)
    Date startTime,endTime;
    @TypeConverters(Converters.class)
   public ArrayList<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Trip(String description, String audioFilePath, Double latitudeStart, Double longitudeStart,
                Double latitudeStop, Double longitudeStop, Date startTime, Date endTime,Double distance) {
        this.description = description;
        this.audioFilePath = audioFilePath;
        this.latitudeStart = latitudeStart;
        this.longitudeStart = longitudeStart;
        this.latitudeStop = latitudeStop;
        this.longitudeStop = longitudeStop;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance=distance;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLatitudeStart() {
        return latitudeStart;
    }

    public void setLatitudeStart(Double latitudeStart) {
        this.latitudeStart = latitudeStart;
    }

    public Double getLongitudeStart() {
        return longitudeStart;
    }

    public void setLongitudeStart(Double longitudeStart) {
        this.longitudeStart = longitudeStart;
    }

    public Double getLatitudeStop() {
        return latitudeStop;
    }

    public void setLatitudeStop(Double latitudeStop) {
        this.latitudeStop = latitudeStop;
    }

    public Double getLongitudeStop() {
        return longitudeStop;
    }

    public void setLongitudeStop(Double longitudeStop) {
        this.longitudeStop = longitudeStop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public void addTags(String text) {
        String[] tags=text.split(" ");
        Boolean found=false;
        for(int i=0;i<tags.length;i++) {
            found=false;
            for (Tag t : getTags()) {
                if (t.text.equals(tags[i])){
                    found=true;
                    break;
                }
            }
            if(!found)
            {
                getTags().add(new Tag(tags[i]));
            }
        }
    }
}
