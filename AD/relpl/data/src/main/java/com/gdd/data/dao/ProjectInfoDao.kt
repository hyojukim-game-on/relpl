package com.gdd.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.gdd.data.model.project.ProjectInfoEntity
import javax.inject.Inject

@Dao
abstract class ProjectInfoDao {
    @Insert
    abstract suspend fun insertProjectInfo(projectInfoEntity: ProjectInfoEntity)

    @Query("DELETE FROM project_info_table")
    abstract suspend fun deleteProjectInfo()

    @Query("SELECT * FROM project_info_table")
    abstract suspend fun getProjectInfo(): ProjectInfoEntity

    @Transaction
    open suspend fun saveProjectInfo(projectInfoEntity: ProjectInfoEntity){
        deleteProjectInfo()
        insertProjectInfo(projectInfoEntity)
    }
}