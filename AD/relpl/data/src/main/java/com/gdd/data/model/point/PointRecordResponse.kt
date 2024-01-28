package com.gdd.data.model.point

data class PointRecordResponse(
    val userTotalCoin: Int,
    val eventList: List<PointRecordItemResponse>
)