package com.ssafy.relpl.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssafy.relpl.db.mongo.entity.TmapRoad
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
import java.math.BigDecimal


@Service
class TmapService {

    val log = LoggerFactory.getLogger(TmapService::class.java)

    @Autowired
    lateinit var tmapRoadRepository: TmapRoadRepository

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

    suspend fun getAllRoads(startLat: BigDecimal, startLng: BigDecimal, endLat: BigDecimal, endLng: BigDecimal) {

        val roadSet = mutableSetOf<Long>()
//        val roadDetailList = mutableListOf<TmapRoad>()
//        val roadHashList = mutableListOf<RoadHash>()
        val pointHashMap = mutableMapOf<Point, Long>()

        var hashVal = 0L

        var lat = startLat
        var count = 0
        var holeCnt = ((startLat - endLat).div(BigDecimal(0.00005)) * ((endLng - startLng).div(BigDecimal(0.00005)))).toInt() + 2
        var i = 0
        var roadHashIndex = 0L
        var apiKey = getkeys().get(i++)
        coroutineScope {
            launch {
                while (lat >= endLat) {
                    lat = lat.minus(BigDecimal(0.00005))
                    var lng = startLng;
                    while (lng <= endLng) {
                        lng = lng.plus(BigDecimal(0.00005))
                        try {
                            ++count
                            if (count % 20000 == 0) {
                                count %= 20000
                                apiKey = getkeys().get(i++)
                            }
                            val responseData = callTmapApi(lat.toDouble(), lng.toDouble(), apiKey)
                            val objectMapper = ObjectMapper()
                            val responseDTO: TmapApiResponseDTO = objectMapper.readValue(responseData, TmapApiResponseDTO::class.java)
                            log.info("count: ${--holeCnt}")
                            responseDTO.resultData.let {
                                if (!roadSet.contains(responseDTO.resultData.header.linkId)) {

                                    roadSet.add(responseDTO.resultData.header.linkId)
                                    log.info(" lat: $lat, lng: $lng, API Call: $responseDTO\"")
//                                    roadDetailList.add(TmapRoad.createRoad(responseDTO, hashVal))
                                    val tmapRoad = TmapRoad.createRoad(responseDTO, hashVal);
                                    insertTmapRoad(tmapRoad)
//                                    insertRoadHash(RoadHash.createRoadHash(hashVal++, responseDTO.resultData.header.linkId))

                                    val start = tmapRoad.geometry.coordinates.first()
                                    val startPoint = geometryFactory.createPoint(Coordinate(start.x, start.y))
                                    var startPointHash = -1L

                                    val end = tmapRoad.geometry.coordinates.last()
                                    val endPoint = geometryFactory.createPoint(Coordinate(end.x, end.y))
                                    var endPointHash = -1L


                                    if (!pointHashMap.contains(startPoint)) {
                                        startPointHash = roadHashIndex
                                        val pointHashEntity = PointHash.createPointHash(roadHashIndex, startPoint)
                                        insertPointHash(pointHashEntity)
                                        pointHashMap.put(startPoint, roadHashIndex++)
                                    } else {
                                        startPointHash = pointHashMap.get(startPoint)!!
                                    }

                                    if (!pointHashMap.contains(endPoint)) {
                                        endPointHash = roadHashIndex
                                        insertPointHash(PointHash.createPointHash(roadHashIndex, endPoint))
                                        pointHashMap.put(endPoint, roadHashIndex++)
                                    } else {
                                        endPointHash = pointHashMap.get(endPoint)!!
                                    }
                                    // point hash 관련 로직 작성 완료
                                    // tmap 데이터 넣는 로직 작성 필요
                                    val roadInfo = RoadInfo.createRoadInfo(tmapRoad.tmapId, startPointHash, endPointHash, tmapRoad.totalDistance)
                                    insertRoadInfo(roadInfo)
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
    }

//    fun insertAllRoads(roads: List<TmapRoad>) {
//        tmapRoadRepository.saveAll(roads)
//    }
//
//    fun insertAllRoadHash(roadsHash: List<RoadHash>) {
//        roadHashRepository.saveAll(roadsHash)
//    }
//
//    fun insertAllPointHash(pointHashList: List<PointHash>){
//        pointHashRepository.saveAll(pointHashList)
//    }
    fun insertPointHash(pointHash: PointHash) {
        pointHashRepository.save(pointHash)
    }

    fun insertRoadHash(roadHash: RoadHash) {
        roadHashRepository.save(roadHash)
    }

    fun insertTmapRoad(tmapRoad: TmapRoad) {
        tmapRoadRepository.save(tmapRoad)
    }

    fun insertRoadInfo(roadInfo: RoadInfo) {
        roadInfoRepository.save(roadInfo)
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

    @Value("\${tmap.api.key6}")
    lateinit var key6: String

    @Value("\${tmap.api.key7}")
    lateinit var key7: String

    @Value("\${tmap.api.key8}")
    lateinit var key8: String

    var keys = mutableListOf<String>()
    fun getkeys(): List<String> {
        if (keys.isEmpty()) keys = mutableListOf(key1, key2, key3, key4, key5, key6, key7, key8)
        return keys
    }
}
