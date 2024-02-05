package com.gdd.relpl.module

import android.content.Context
import androidx.room.Room
import com.gdd.data.RelplDatabase
import com.gdd.data.dao.LocationTrackingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun providesRelplDatabase(
        @ApplicationContext context: Context
    ): RelplDatabase {
        return Room.databaseBuilder(
            context,
            RelplDatabase::class.java,
            "relpl_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesLocationTrackingDao(
        relplDatabase: RelplDatabase
    ): LocationTrackingDao {
        return relplDatabase.locationTrackingDao()
    }
}