package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.PointHash;
import com.ssafy.relpl.db.postgre.repository.PointHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointHashService {
    private final PointHashRepository pointHashRepository;


    public PointHash getNearPoint(double x, double y) {
        return pointHashRepository.findFirstByPointCoordinate(x, y);
    }

    public int countAllPointHash() {
        return pointHashRepository.countAllPointHash();
    }
}