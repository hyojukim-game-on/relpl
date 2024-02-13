package com.gdd.data.repository.report

import com.gdd.data.mapper.toReportRecord
import com.gdd.data.model.PointResponse
import com.gdd.data.model.UserIdRequest
import com.gdd.data.model.report.ReportRegistRequest
import com.gdd.data.repository.report.remote.ReportRemoteDataSource
import com.gdd.domain.model.report.ReportRecord
import com.gdd.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportRemoteDataSource: ReportRemoteDataSource
): ReportRepository {
    override suspend fun registReport(
        userId: Long,
        reportDate: String,
        latitude: Double,
        longitude: Double
    ): Result<Boolean> {
        return reportRemoteDataSource.registReport(
            ReportRegistRequest(
                userId,
                reportDate,
                PointResponse(longitude,latitude)
            )
        )
    }

    override suspend fun getReportRecordList(userId: Long): Result<List<ReportRecord>> {
        return reportRemoteDataSource.getReportRecordList(UserIdRequest(userId)).map {list ->
            list.map {
                it.toReportRecord()
            }
        }
    }
}