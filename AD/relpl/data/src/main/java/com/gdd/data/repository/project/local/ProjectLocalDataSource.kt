package com.gdd.data.repository.project.local

import com.gdd.data.model.project.ProjectInfoEntity

interface ProjectLocalDataSource {
    suspend fun saveProjectInfo(id: Long,
                                name: String,
                                totalContributer: Int,
                                totalDistance: Int,
                                remainDistance: Int,
                                createDate: String,
                                endDate: String,
                                isPath: Boolean,
                                endLatitude: Double,
                                endLongitude: Double)

    suspend fun getProjectInfo():ProjectInfoEntity
}