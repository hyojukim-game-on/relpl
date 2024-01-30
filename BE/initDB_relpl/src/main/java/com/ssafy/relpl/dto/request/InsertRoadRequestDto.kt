package com.ssafy.relpl.dto.request

import org.springframework.data.geo.Point

data class InsertRoadRequestDto(var startPoint: Point
                                ,var endPoint: Point,
                                var callApiPassword: String)
