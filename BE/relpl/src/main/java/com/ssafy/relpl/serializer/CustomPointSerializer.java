package com.ssafy.relpl.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


import org.locationtech.jts.geom.Point;

// CustomPointSerializer.java
public class CustomPointSerializer extends JsonSerializer<Point> {
    @Override
    public void serialize(Point point, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("x", point.getX());
        jsonGenerator.writeNumberField("y", point.getY());
        jsonGenerator.writeEndObject();
    }
}
