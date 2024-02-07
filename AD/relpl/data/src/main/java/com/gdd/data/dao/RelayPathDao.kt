package com.gdd.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gdd.data.model.tracking.LocationTrackingEntity
import com.gdd.data.model.tracking.RelayPathEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelayPathDao {
    @Insert
    suspend fun insertRelayPathList(relayPathEntityList: List<RelayPathEntity>)

    @Query("SELECT * FROM relay_path_table")
    fun getAllRelayPathData(): Flow<List<RelayPathEntity>>

    @Query("DELETE FROM relay_path_table")
    suspend fun deleteAllRelayPathData()
}