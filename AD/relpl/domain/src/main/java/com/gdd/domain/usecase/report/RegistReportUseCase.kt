package com.gdd.domain.usecase.report

import com.gdd.domain.repository.ReportRepository
import javax.inject.Inject

class RegistReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(
        userId: Long,
        reportDate: String,
        latitude: Double,
        longitude: Double
    ): Result<Boolean>{
        return reportRepository.registReport(userId, reportDate, latitude, longitude)
    }
}