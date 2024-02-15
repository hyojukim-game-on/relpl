package com.gdd.data.repository.project.local

import com.gdd.data.dao.ProjectInfoDao
import com.gdd.data.model.project.ProjectInfoEntity
import javax.inject.Inject

class ProjectLocalDataSourceImpl @Inject constructor(
    private val projectInfoDao: ProjectInfoDao
) : ProjectLocalDataSource {
    override suspend fun saveProjectInfo(
        id: Long,
        name: String,
        totalContributer: Int,
        totalDistance: Int,
        remainDistance: Int,
        createDate: String,
        endDate: String,
        isPath: Boolean,
        endLatitude: Double,
        endLongitude: Double
    ) {
        projectInfoDao.saveProjectInfo(
            ProjectInfoEntity(
                id,
                name,
                totalContributer,
                totalDistance,
                remainDistance,
                createDate,
                endDate,
                isPath,
                endLatitude,
                endLongitude
            )
        )
    }

    override suspend fun getProjectInfo(): ProjectInfoEntity {
        return projectInfoDao.getProjectInfo()
    }
}