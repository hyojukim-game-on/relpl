package com.gdd.data.model.report

import com.gdd.data.model.PointResponse

data class ReportRegistRequest(
    val userId: Long,
    val reportDate: String, // yyyy-MM-dd
    val reportCoordinate: PointResponse
)
