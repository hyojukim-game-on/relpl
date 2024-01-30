package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.RoadInfo;
import com.ssafy.relpl.db.postgre.repository.RoadInfoRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@NoArgsConstructor
public class RoadInfoService {

    RoadInfoRepository roadInfoRepository;
    public List<RoadInfo> getRoadInfo() {
        return roadInfoRepository.findAll();
    }

}
