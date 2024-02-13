package com.gdd.data.repository.report.remote

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.UserIdRequest
import com.gdd.data.model.report.ReportRecordResponse
import com.gdd.data.model.report.ReportRegistRequest

interface ReportRemoteDataSource {
    suspend fun registReport(reportRegistRequest: ReportRegistRequest): Result<Boolean>

    suspend fun getReportRecordList(userIdRequest: UserIdRequest): Result<List<ReportRecordResponse>>
}