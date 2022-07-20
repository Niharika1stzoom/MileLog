package com.firstzoom.milelog.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class TempLocationList {
    int id;
    List<Location> locations;
    //=new ArrayList<>();

    public TempLocationList(int id) {
        this.id = id;
        locations=new ArrayList<Location>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
