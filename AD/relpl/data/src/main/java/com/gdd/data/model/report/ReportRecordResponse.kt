package com.gdd.data.model.report

import com.gdd.data.model.PointResponse

data class ReportRecordResponse(
    val reportDate: String,
    val reportCoordinate: PointResponse
)
