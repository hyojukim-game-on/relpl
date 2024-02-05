package com.ssafy.relpl.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.relpl.db.postgre.entity.RoadHash;
import com.ssafy.relpl.dto.response.TmapApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmapService {

    @Value("${tmap.api.key}")
    private String apiKey;  // application.properties 또는 application.yml에서 설정한 Tmap API 키
    private final String baseUrl = "https://apis.openapi.sk.com/tmap/road/nearToRoad";

    public TmapApiResponse callTmapApi(double longitude, double latitude){

        try {
            String url = baseUrl + "?version=1&appKey=" + apiKey + "&lat=" + latitude + "&lon=" + longitude;

            RestTemplate restTemplate = new RestTemplate();
            String responseStr = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            TmapApiResponse tmapApiResponse = objectMapper.readValue(responseStr, TmapApiResponse.class);
            return tmapApiResponse;
        } catch (Exception e) {
            log.error("tmap api 조회 실패", e);
            return null;
        }
    }
}
