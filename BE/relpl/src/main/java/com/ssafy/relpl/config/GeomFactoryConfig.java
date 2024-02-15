package com.ssafy.relpl.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeomFactoryConfig {
    @Bean
    public GeometryFactory getGeometryFactory() {
        return new GeometryFactory();
    }
}
