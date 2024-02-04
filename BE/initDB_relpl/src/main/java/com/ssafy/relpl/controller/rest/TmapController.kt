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
import java.math.BigDecimal

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
                    tmapService.getAllRoads(BigDecimal(insertRoadRequestDto.startPoint.y)
                            , BigDecimal(insertRoadRequestDto.startPoint.x)
                            , BigDecimal(insertRoadRequestDto.endPoint.y)
                            , BigDecimal(insertRoadRequestDto.endPoint.x))
//                    val pointHash = tmapService.insertAllRoadInfo(tmapData)
//                    log.info("insertAllRoadHash 완료")
//                    tmapService.insertAllRoads(tmapData.roads)
//                    log.info("insertAllRoads 완료")
//                    tmapService.insertAllRoadHash(tmapData.roadsHash)
//                    log.info("insertAllRoadHash 완료")
//                    tmapService.insertAllPointHash(pointHash)
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
        val startLat = BigDecimal(timesRoadRequestDto.startPoint.y)
        val startLng = BigDecimal(timesRoadRequestDto.startPoint.x)
        val endLat = BigDecimal(timesRoadRequestDto.endPoint.y)
        val endLng = BigDecimal(timesRoadRequestDto.endPoint.x)
        var holeCnt = ((startLat - endLat).div(BigDecimal(0.00005)) * ((endLng - startLng).div(BigDecimal(0.00005)))).toInt() + 2
        return ResponseEntity.ok().body(CommonResponse.OK("총 횟수 : $holeCnt", true))
    }
}