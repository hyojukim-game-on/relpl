package com.gdd.domain.repository

interface ReportRepository{
    suspend fun registReport(
        userId: Long,
        reportDate: String,
        latitude: Double,
        longitude: Double
    ):Result<Boolean>
}