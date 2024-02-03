package com.ssafy.relpl.config;

import com.ssafy.relpl.db.postgre.entity.RoadHash;
import jakarta.annotation.PostConstruct;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeomFactoryConfig {
    @Bean
    public GeometryFactory getGeometryFactory() {
        return new GeometryFactory();
    }
}
