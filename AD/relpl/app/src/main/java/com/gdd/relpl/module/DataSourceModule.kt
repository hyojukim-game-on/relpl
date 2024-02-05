package com.gdd.relpl.module

import com.gdd.data.repository.project.remote.ProjectRemoteDataSource
import com.gdd.data.repository.project.remote.ProjectRemoteDataSourceImpl
import com.gdd.data.repository.rank.remote.RankRemoteDataSourceImpl
import com.gdd.data.repository.report.remote.ReportRemoteDataSource
import com.gdd.data.repository.report.remote.ReportRemoteDataSourceImpl
import com.gdd.data.repository.user.UserRepositoryImpl
import com.gdd.data.repository.user.remote.UserRemoteDataSource
import com.gdd.data.repository.user.remote.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindUserRemoteDataSource(userRemoteDataSourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindProjectRemoteDataSource(projectRemoteDataSourceImpl: ProjectRemoteDataSourceImpl): ProjectRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindReportRemoteDataSource(remoteDataSourceImpl: ReportRemoteDataSourceImpl): ReportRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindRankRemoteDataSource(rankRemoteDataSourceImpl: RankRemoteDataSourceImpl): RankRemoteDataSourceImpl
}