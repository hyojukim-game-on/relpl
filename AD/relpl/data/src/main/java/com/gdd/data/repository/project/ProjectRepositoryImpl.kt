package com.gdd.data.repository.project

import com.gdd.data.repository.project.remote.ProjectRemoteDataSource
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectRemoteDataSource: ProjectRemoteDataSource
) : ProjectRepository{
}