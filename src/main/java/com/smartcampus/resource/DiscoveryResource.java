package com.smartcampus.resource;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Smart Campus API");
        info.put("version", "1.0");
        info.put("description", "REST API for managing campus rooms and sensors");
        info.put("contact", "w2020906@my.westminster.ac.uk");

        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        info.put("resources", links);

        return Response.ok(info).build();
    }
}