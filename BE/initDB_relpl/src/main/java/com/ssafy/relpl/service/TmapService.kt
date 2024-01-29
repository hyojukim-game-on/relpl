package com.ssafy.relpl.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssafy.relpl.db.mongo.entity.Road
import com.ssafy.relpl.db.mongo.repository.TmapRoadRepository
import com.ssafy.relpl.db.postgre.entity.PointHash
import com.ssafy.relpl.db.postgre.entity.RoadHash
import com.ssafy.relpl.db.postgre.entity.RoadInfo
import com.ssafy.relpl.db.postgre.repository.PointHashRepository
import com.ssafy.relpl.db.postgre.repository.RoadHashRepository
import com.ssafy.relpl.db.postgre.repository.RoadInfoRepository
import com.ssafy.relpl.dto.response.TmapApiResponseDTO
import com.ssafy.relpl.util.common.TmapData
import kotlinx.coroutines.*
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class TmapService {

    val log = LoggerFactory.getLogger(TmapService::class.java)

    @Autowired
    lateinit var roadRepository: TmapRoadRepository

    @Autowired
    lateinit var roadHashRepository: RoadHashRepository

    @Autowired
    lateinit var pointHashRepository: PointHashRepository

    @Autowired
    lateinit var roadInfoRepository: RoadInfoRepository

    private val baseUrl = "https://apis.openapi.sk.com/tmap/road/nearToRoad"

    private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
    fun callTmapApi(latitude: Double, longitude: Double, apiKey: String): String? {
        val url = "$baseUrl?version=1&appKey=$apiKey&lat=$latitude&lon=$longitude"
        val restTemplate = RestTemplate()
        restTemplate.getForObject(url, String::class.java).let {
            return it ?: null
        }
    }

    suspend fun getAllRoads(startLat: Double, startLng: Double, endLat: Double, endLng: Double): TmapData {

        val roadSet = mutableSetOf<Long>()
        val roadDetailList = mutableListOf<Road>()
        val roadHashList = mutableListOf<RoadHash>()
        var hashVal = 0L

        var lat = startLat
        var count = 0
        var holeCnt = ((startLat - endLat) / 0.00005 * ((endLng - startLng) / 0.00005)).toInt() + 2
        var i = 0
        var apiKey = getInstance().get(i++)
        coroutineScope {
            launch {
                while (lat >= endLat) {
                    lat -= 0.00005
                    var lng = startLng;
                    while (lng <= endLng) {
                        lng += 0.00005
                        try {
                            ++count
                            if (count % 20000 == 0) {
                                count %= 20000
                                apiKey = getInstance().get(i++)
                            }
                            val responseData = callTmapApi(lat, lng, apiKey)
                            val objectMapper = ObjectMapper()
                            val responseDTO: TmapApiResponseDTO = objectMapper.readValue(responseData, TmapApiResponseDTO::class.java)
                            log.info("count: ${--holeCnt}")
                            responseDTO.resultData.let {
                                if (roadSet.add(responseDTO.resultData.header.linkId)) {
                                    log.info(" lat: $lat, lng: $lng, API Call: $responseDTO\"")
                                    roadDetailList.add(Road.createRoad(responseDTO))
                                    roadHashList.add(RoadHash.createRoadHash(hashVal++, responseDTO.resultData.header.linkId))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            log.info(e.message)
                        }
                    }

                }
            }
        }
        return TmapData(roadDetailList, roadHashList)
    }

    fun insertAllRoads(roads: List<Road>) {
        roadRepository.saveAll(roads)
    }

    fun insertAllRoadHash(roadsHash: List<RoadHash>) {
        roadHashRepository.saveAll(roadsHash)
    }

    fun insertAllPointHash(pointHashList: List<PointHash>){

//        val pointHashList = mutableListOf<PointHash>()
//        for ((index, value) in pointSet.withIndex()) {
//            pointHashList.add(PointHash.createPointHash(index.toLong(), value))
//        }
        pointHashRepository.saveAll(pointHashList)
    }

    fun insertAllRoadInfo(tmapData: TmapData) : List<PointHash>{

        val tmapToRoadHashMap = mutableMapOf<Long, Long>()
        val roadHashList = tmapData.roadsHash

        for (roadHash in roadHashList) {
            tmapToRoadHashMap.put(roadHash.tmapId, roadHash.roadHashId)
        }


        val pointSet = mutableSetOf<Point>()
        val indexPointMap = mutableMapOf<Point, Long>()
        val pointHashList = mutableListOf<PointHash>();
        var index = 0L

        val roadInfoList = mutableListOf<RoadInfo>()

        for (road in tmapData.roads) {

            val start = road.geometry.coordinates.first()
            val startPoint = geometryFactory.createPoint(Coordinate(start.x, start.y))
            var startPointHash = -1L

            val end = road.geometry.coordinates.last()
            val endPoint = geometryFactory.createPoint(Coordinate(end.x, end.y))
            var endPointHash = -1L

            if (pointSet.add(startPoint)) {
                startPointHash = index
                indexPointMap.put(startPoint, index)
                pointHashList.add(PointHash.createPointHash(index++, startPoint))
            } else {
                startPointHash = indexPointMap.get(startPoint)!!
            }

            if (pointSet.add(endPoint)) {
                endPointHash = index
                indexPointMap.put(endPoint, index)
                pointHashList.add(PointHash.createPointHash(index++, endPoint))
            } else {
                endPointHash = indexPointMap.get(endPoint)!!
            }
            roadInfoList.add(RoadInfo.createRoadInfo(tmapToRoadHashMap.get(road.tmap_id), startPointHash, endPointHash, road.total_distance))
            log.info(roadInfoList.last().toString())
        }
        roadInfoRepository.saveAll(roadInfoList)
        return pointHashList
    }

    @Value("\${tmap.api.key1}")
    lateinit var key1: String

    @Value("\${tmap.api.key2}")
    lateinit var key2: String

    @Value("\${tmap.api.key3}")
    lateinit var key3: String

    @Value("\${tmap.api.key4}")
    lateinit var key4: String

    @Value("\${tmap.api.key5}")
    lateinit var key5: String

    var keys = mutableListOf<String>()
    fun getInstance(): List<String> {
        if (keys.isEmpty()) keys = mutableListOf(key1, key2, key3, key4, key5)
        return keys
    }
}
