package com.gdd.presentation.model.mapper

import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.presentation.model.RelayPath

fun RelayPath.toRelayPathData(): RelayPathData{
    return RelayPathData(
        latLng.latitude,
        latLng.longitude,
        myVisit, beforeVisit
    )
}