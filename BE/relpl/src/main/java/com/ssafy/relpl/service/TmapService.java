package com.ssafy.relpl.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TmapService {

//    private final RoadRepository roadRepository;
//    private final RoadHashRepository roadHashRepository;
//
//    @Value("${tmap.api.key}")
//    private String apiKey;  // application.properties 또는 application.yml에서 설정한 Tmap API 키
//
//    private final String baseUrl = "https://apis.openapi.sk.com/tmap/road/nearToRoad";
//
//    public String callTmapApi(double latitude, double longitude) {
//        String url = baseUrl + "?version=1&appKey=" + apiKey + "&lat=" + latitude + "&lon=" + longitude;
//
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate.getForObject(url, String.class);
//    }
//
//    public Road saveRoad(TmapApiResponseDTO responseDTO) {
//        Road road = Road.createRoad(responseDTO);
//        Road saved = roadRepository.save(road);
//        return saved;
//    }
//
//    public RoadHash saveRoadHash(Long id, Long tmap_id) {
//        RoadHash roadHash = RoadHash.createRoadHash(id, tmap_id);
//        RoadHash saved = roadHashRepository.save(roadHash);
//        return saved;
//    }
//
//    public List<Road> getAllRoads() {
//        return roadRepository.findAll();
//    }
//
//    public Road getRoad(String id) {
//        Optional<Road> road = roadRepository.findById(id);
//        if(road.isPresent()) return road.get();
//
//        return null;
//    }
}
