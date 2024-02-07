package com.gdd.relpl.module

import android.content.Context
import androidx.room.Room
import com.gdd.data.RelplDataBase
import com.gdd.data.dao.LocationTrackingDao
import com.gdd.data.dao.ProjectInfoDao
import com.gdd.data.dao.RelayPathDao
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
    ): RelplDataBase {
        return Room.databaseBuilder(
            context,
            RelplDataBase::class.java,
            "relpl_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesLocationTrackingDao(
        relplDatabase: RelplDataBase
    ): LocationTrackingDao {
        return relplDatabase.locationTrackingDao()
    }

    @Provides
    @Singleton
    fun providesRelayPathDao(
        relplDatabase: RelplDataBase
    ): RelayPathDao {
        return relplDatabase.relayPathDao()
    }

    @Provides
    @Singleton
    fun providesProjectInfoDao(
        relplDataBase: RelplDataBase
    ): ProjectInfoDao {
        return relplDataBase.projectInfoDao()
    }
}