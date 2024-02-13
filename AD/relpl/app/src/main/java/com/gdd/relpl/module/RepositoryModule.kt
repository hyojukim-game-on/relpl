package com.gdd.relpl.module

import com.gdd.data.repository.fcm.FcmRepositoryImpl
import com.gdd.data.repository.project.ProjectRepositoryImpl
import com.gdd.data.repository.rank.RankRepositoryImpl
import com.gdd.data.repository.report.ReportRepositoryImpl
import com.gdd.data.repository.tracking.LocationTrackingRepositoryImpl
import com.gdd.data.repository.user.UserRepositoryImpl
import com.gdd.domain.repository.FcmRepository
import com.gdd.domain.repository.LocationTrackingRepository
import com.gdd.domain.repository.ProjectRepository
import com.gdd.domain.repository.RankRepository
import com.gdd.domain.repository.ReportRepository
import com.gdd.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindProjectRepository(projectRepositoryImpl: ProjectRepositoryImpl): ProjectRepository

    @Singleton
    @Binds
    abstract fun bindReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository

    @Singleton
    @Binds
    abstract fun bindRankRepository(rankRepositoryImpl: RankRepositoryImpl): RankRepository

    @Singleton
    @Binds
    abstract fun bindLocationTrackingRepository(locationTrackingRepositoryImpl: LocationTrackingRepositoryImpl): LocationTrackingRepository

    @Singleton
    @Binds
    abstract fun bindFcmRepository(fcmRepositoryImpl: FcmRepositoryImpl): FcmRepository

}