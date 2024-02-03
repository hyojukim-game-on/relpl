package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.business.ProjectRecommendBusiness;
import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.dto.request.ProjectRecommendRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/project")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProjectController {

    private final ProjectRecommendBusiness projectRecommendBusiness;
    private final GeomFactoryConfig geomFactoryConfig;
    @PostMapping("/recommend")
    public ResponseEntity<?> recommendProject(@RequestBody ProjectRecommendRequestDto request) {
        Point startPoint = geomFactoryConfig.getGeometryFactory().createPoint(
                new Coordinate(request.getStartPoint().getX(), request.getStartPoint().getY()
                )
        );
        Point endPoint = geomFactoryConfig.getGeometryFactory().createPoint(
                new Coordinate(request.getEndPoint().getX(), request.getEndPoint().getY()
                )
        );
        return projectRecommendBusiness.recommendProject(startPoint, endPoint);
    }

}
