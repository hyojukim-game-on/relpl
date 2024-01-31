package com.gdd.data.repository.project.remote

import com.gdd.data.api.ProjectService
import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.MarkerResponse
import com.gdd.data.toNonDefault
import javax.inject.Inject

class ProjectRemoteDataSourceImpl @Inject constructor(
    private val projectService: ProjectService
): ProjectRemoteDataSource {
    override suspend fun isExistProject(lat: Double, lng: Double): Result<Boolean> {
        return projectService.isExistProject(lat, lng)
            .toNonDefault()
            .map {
                it.isExist
            }
    }

    override suspend fun getAllAvailableProject(): Result<List<MarkerResponse>> {
        return projectService.getAllAvailableProject().toNonDefault()
    }

    override suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceProjectResponse> {
        return projectService.getDistanceProjectInfo(projectId).toNonDefault()
    }
}