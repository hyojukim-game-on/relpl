package com.gdd.data.mapper

import com.gdd.data.model.PointResponse
import com.gdd.domain.model.Point

fun Point.toPointResponse(): PointResponse{
    return PointResponse(this.x, this.y)
}