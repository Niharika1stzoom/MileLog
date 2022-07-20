package com.firstzoom.milelog.di;

import android.content.Context;

import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.repository.Repository;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.room.LocationPathDao;
import com.firstzoom.milelog.room.TripDao;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    @Provides
    TripDao provideRepoDao(AppDatabase database) {
        return database.tripDao();
    }

    @Provides
    LocationPathDao provideLocationPathDao(AppDatabase database) {
        return database.locationPathDao();
    }

    @Singleton
    @Provides
    AppDatabase provideDatabase(@ApplicationContext Context context) {
        return AppDatabase.getDatabase(context);
    }
    @Singleton
    @Provides
    Repository provideRepository(TripDao tripDao,LocationPathDao locationPathDao){
        return new Repository(tripDao,locationPathDao);
    }


}
