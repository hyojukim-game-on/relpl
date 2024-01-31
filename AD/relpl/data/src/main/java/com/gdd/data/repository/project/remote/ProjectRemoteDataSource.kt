package com.gdd.data.repository.project.remote

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.MarkerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProjectRemoteDataSource {
    suspend fun isExistProject(lat: Double, lng: Double): Result<Boolean>

    suspend fun getAllAvailableProject(): Result<List<MarkerResponse>>

    suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceProjectResponse>
}