package com.ssafy.relpl.util.common

import com.ssafy.relpl.db.mongo.entity.Road
import com.ssafy.relpl.db.postgre.entity.RoadHash

data class RoadData(val roads: List<Road>, val roadsHash: List<RoadHash>)
