package com.gdd.domain.repository

import com.gdd.domain.model.report.ReportRecord

interface ReportRepository{
    suspend fun registReport(
        userId: Long,
        reportDate: String,
        latitude: Double,
        longitude: Double
    ):Result<Boolean>

    suspend fun getReportRecordList(userId: Long): Result<List<ReportRecord>>

}