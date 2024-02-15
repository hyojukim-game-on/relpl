package com.gdd.domain.usecase.report

import com.gdd.domain.model.report.ReportRecord
import com.gdd.domain.repository.ReportRepository
import javax.inject.Inject

class GetReportRecordListUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<ReportRecord>>{
        return reportRepository.getReportRecordList(userId)
    }
}