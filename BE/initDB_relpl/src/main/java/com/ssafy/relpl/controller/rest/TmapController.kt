package com.ssafy.relpl.controller.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssafy.relpl.dto.response.CommonResponse
import com.ssafy.relpl.dto.response.TmapApiResponseDTO
import com.ssafy.relpl.service.TmapService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("/api/tmap")
class TmapController {

    val log = LoggerFactory.getLogger(TmapController::class.java)

    @Autowired
    private lateinit var tmapService: TmapService

    @GetMapping("/insertTmapData")
    suspend fun insertTmapData(@RequestParam startLat: Double,
                               @RequestParam startLng: Double,
                               @RequestParam endLat: Double,
                               @RequestParam endLng: Double
    ): ResponseEntity<Any?> {

        log.info("StartLat: $startLat, StartLng: $startLng, EndLat: $endLat, EndLng: $endLng")
//        var roads = mutableMapOf<String, TmapApiResponseDTO>()
        try {
            coroutineScope {
                launch {
                    val tmapData = tmapService.getAllRoads(startLat, startLng, endLat, endLng)
                    val pointHash = tmapService.insertAllRoadInfo(tmapData)
                    log.info("insertAllRoadHash 완료")

                    tmapService.insertAllRoads(tmapData.roads)
                    log.info("insertAllRoads 완료")
                    tmapService.insertAllRoadHash(tmapData.roadsHash)
                    log.info("insertAllRoadHash 완료")
                    tmapService.insertAllPointHash(pointHash)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.internalServerError().body("${e.message}")
        }
        return ResponseEntity.ok().body(CommonResponse.OK("Tmap 데이터 저장 완료"))
    }

    @GetMapping("/times")
    fun getTimes(@RequestParam startLat: Double,
                 @RequestParam startLng: Double,
                 @RequestParam endLat: Double,
                 @RequestParam endLng: Double) : ResponseEntity<Any?> {
        val count = ((startLat - endLat) / 0.0001 * ((endLng - startLng) / 0.0001)).toInt() + 2
        return ResponseEntity.ok().body(CommonResponse.OK("총 횟수 : $count"))
    }
}