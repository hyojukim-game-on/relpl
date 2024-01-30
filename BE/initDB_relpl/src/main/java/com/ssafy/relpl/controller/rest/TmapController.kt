package com.ssafy.relpl.controller.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssafy.relpl.dto.request.InsertRoadRequestDto
import com.ssafy.relpl.dto.request.TimesRoadRequestDto
import com.ssafy.relpl.dto.response.CommonResponse
import com.ssafy.relpl.service.TmapService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.geo.Point
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sub/tmap")
class TmapController {

    val log = LoggerFactory.getLogger(TmapController::class.java)

    @Autowired
    private lateinit var tmapService: TmapService

    @Value("\${tmap.apiPassword}")
    private lateinit var apiPassword: String

    @PostMapping("/insertTmapData")
    suspend fun insertTmapData(@RequestBody insertRoadRequestDto: InsertRoadRequestDto
    ): ResponseEntity<Any?> {

        log.info("StartPoint: ${insertRoadRequestDto.startPoint}, EndPoint: ${insertRoadRequestDto.endPoint}")
        if (insertRoadRequestDto.callApiPassword != apiPassword) return ResponseEntity.badRequest().body("패스워드 불일치")
        try {
            coroutineScope {
                launch {
                    val tmapData = tmapService.getAllRoads(
                            insertRoadRequestDto.startPoint.y
                            , insertRoadRequestDto.startPoint.x
                            , insertRoadRequestDto.endPoint.y
                            , insertRoadRequestDto.endPoint.x)
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
        return ResponseEntity.ok().body(CommonResponse.OK("Tmap 데이터 저장 완료", true))
    }

    @PostMapping("/times")
    fun getTimes(@RequestBody timesRoadRequestDto: TimesRoadRequestDto) : ResponseEntity<Any?> {
        log.info("start: {}", timesRoadRequestDto.startPoint)
        log.info("start: {}", timesRoadRequestDto.endPoint)
        val count = ((timesRoadRequestDto.startPoint.y - timesRoadRequestDto.endPoint.y) / 0.00005 * ((timesRoadRequestDto.endPoint.x - timesRoadRequestDto.startPoint.x) / 0.00005)).toInt()
        return ResponseEntity.ok().body(CommonResponse.OK("총 횟수 : $count", true))
    }
}