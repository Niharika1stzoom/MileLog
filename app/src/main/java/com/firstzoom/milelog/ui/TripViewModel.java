package com.firstzoom.milelog.ui;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Trip;
import com.firstzoom.milelog.repository.Repository;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TripViewModel extends AndroidViewModel {
    Context mContext;
    private LiveData<List<Trip>> mTripList=new MutableLiveData<>();
    @Inject
    Repository repository;
    @Inject
    public TripViewModel(@NonNull Application application) {
        super(application);
        mContext=application.getApplicationContext();
    }

    public LiveData<List<Trip>> getTripList(Date date) {
        mTripList=repository.getTripList(date);
        return mTripList;
    }
    public LiveData<List<Trip>> getTripList() {
        mTripList=repository.getTripList();
        return mTripList;
    }

    public LiveData<Trip> getTrip(int id) {
        return repository.getTrip(id);
    }

    public void updateTrip(Trip mTrip) {
        repository.insertTrip(mTrip);
    }

    public void delTrip(Trip trip) {
        repository.delTrip(trip);
    }

    public LiveData<List<LocationPath>> getLocationPaths() {
        return repository.getLocationPath();
    }

    public void insertTrip(Trip trip) {
        repository.insertTrip(trip);
    }

    public void insertPath(LocationPath path) {
        repository.insertLocationPath(path);
    }
}