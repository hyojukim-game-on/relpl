package com.gdd.presentation.model.mapper

import com.gdd.domain.model.tracking.TrackingData
import com.gdd.domain.model.report.ReportRecord
import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.presentation.model.RelayPath
import com.gdd.presentation.model.ReportRecordPoint
import com.gdd.presentation.model.TrackingPoint
import com.naver.maps.geometry.LatLng

fun ReportRecord.toReportRecordPoint(): ReportRecordPoint {
    return ReportRecordPoint(
        date,
        LatLng(point.y, point.x)
    )
}

fun TrackingData.toTrackingPoint(): TrackingPoint {
    return TrackingPoint(
        timeMillis,
        LatLng(latitude,longitude)
    )
}

fun RelayPathData.toRelayPath(): RelayPath{
    return RelayPath(
        LatLng(latitude, longitude),
        visit
    )
}