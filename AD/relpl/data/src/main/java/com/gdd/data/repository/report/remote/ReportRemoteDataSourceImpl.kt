package com.gdd.data.repository.report.remote

import com.gdd.data.api.ReportService
import com.gdd.data.model.UserIdRequest
import com.gdd.data.model.report.ReportRecordResponse
import com.gdd.data.model.report.ReportRegistRequest
import com.gdd.data.toNonDefault
import javax.inject.Inject

class ReportRemoteDataSourceImpl @Inject constructor(
    private val reportService: ReportService
): ReportRemoteDataSource {
    override suspend fun registReport(reportRegistRequest: ReportRegistRequest): Result<Boolean> {
        return reportService.registReport(reportRegistRequest).toNonDefault()
    }

    override suspend fun getReportRecordList(userIdRequest: UserIdRequest): Result<List<ReportRecordResponse>> {
        return reportService.getReportRecordList(userIdRequest).toNonDefault()
    }
}