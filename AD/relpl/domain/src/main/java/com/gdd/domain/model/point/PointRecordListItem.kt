package com.gdd.domain.model.point

/**
 * @param eventDate "yyyy-MM-dd HH:mm"
 */
data class PointRecordListItem(
    val eventDate: String,
    val amount: Int,
    val eventDetail: String
)
