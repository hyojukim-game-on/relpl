package com.gdd.data.repository.report.remote

import com.gdd.data.model.report.ReportRegistRequest

interface ReportRemoteDataSource {
    suspend fun registReport(reportRegistRequest: ReportRegistRequest): Result<Boolean>
}