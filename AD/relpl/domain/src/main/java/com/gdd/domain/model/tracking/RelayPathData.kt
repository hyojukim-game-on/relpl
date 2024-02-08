package com.gdd.domain.model.tracking

data class RelayPathData(
    val latitude: Double,
    val longitude: Double,
    val myVisit: Boolean,
    val beforeVisit: Boolean
)