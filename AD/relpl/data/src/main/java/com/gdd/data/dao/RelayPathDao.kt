package com.gdd.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gdd.data.model.tracking.RelayPathEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelayPathDao {
    @Insert
    suspend fun insertRelayPathList(relayPathEntityList: List<RelayPathEntity>)

    @Query("SELECT * FROM relay_path_table")
    fun getAllRelayPathData(): Flow<List<RelayPathEntity>>

    @Query("SELECT * FROM relay_path_table")
    suspend fun getAllRelayPathDataOnce(): List<RelayPathEntity>

    @Query("DELETE FROM relay_path_table")
    suspend fun deleteAllRelayPathData()

    @Update
    suspend fun updateRelayPathPoint(relayPathEntity: RelayPathEntity)
}