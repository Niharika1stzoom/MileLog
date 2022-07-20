package com.firstzoom.milelog.util;

import android.location.Location;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.room.AppDatabase;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class DistanceUtil {
    public static Double   calDistance(List<LocationPath> location)
    {
        ArrayList<LatLng> latLngs=new ArrayList();
        for (LocationPath loc :  location){
            if(loc.getLatitude()!=null && loc.getLongitude()!=null)
                latLngs.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }
        Double dist = SphericalUtil.computeLength(latLngs)/1000;
        return dist;

    }
    public static Double  calDistance(Double lat1,Double long1,Double lat2,Double long2)
    {
        if(lat1!=null && long1!=null && lat2!=null && long2!=null){
            ArrayList<LatLng> latLngs=new ArrayList();
            latLngs.add(new LatLng(lat1,long1));
            latLngs.add(new LatLng(lat2,long2));
            Double dist = SphericalUtil.computeLength(latLngs);
            return dist/1000;}
        return 0.0;
    }

}
