package com.gdd.presentation.model.mapper

import com.gdd.domain.model.relay.RelayInfoData
import com.gdd.domain.model.report.ReportRecord
import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.model.tracking.TrackingData
import com.gdd.presentation.model.RelayInfo
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
        myVisit,beforeVisit
    )
}

fun RelayInfoData.toRelayInfo(): RelayInfo{
    return RelayInfo(
        id,
        name,
        totalContributer,
        totalDistance,
        remainDistance,
        createDate,
        endDate,
        isPath,
        LatLng(
            endPoint.y,
            endPoint.x
        )
    )
}



fun Int.toStringDistance(): String{
    val km = if (this / 1000 > 0) "${this/1000}km" else ""
    val m = "${this%1000}m"
    return "$km $m"
}