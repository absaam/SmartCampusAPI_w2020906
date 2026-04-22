package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // Get all sensors - can filter by type using ?type=CO2
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.getSensors().values());
        if (type != null && !type.isEmpty()) {
            List<Sensor> matchingSensors = new ArrayList<>();
            for (Sensor s : allSensors) {
                if (s.getType().equalsIgnoreCase(type)) {
                    matchingSensors.add(s);
                }
            }
            return Response.ok(matchingSensors).build();
        }
        return Response.ok(allSensors).build();
    }

    // Get one sensor by ID
    @GET
    @Path("{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor foundSensor = DataStore.getSensors().get(sensorId);
        if (foundSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No sensor found with that ID\"}")
                    .build();
        }
        return Response.ok(foundSensor).build();
    }

    // Add a new sensor - checks the room exists first
    @POST
    public Response createSensor(Sensor sensor) {
        if (!DataStore.getRooms().containsKey(sensor.getRoomId())) {
            return Response.status(422)
                    .entity("{\"error\":\"The roomId provided does not match any existing room\"}")
                    .build();
        }
        if (DataStore.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"A sensor with this ID already exists\"}")
                    .build();
        }
        DataStore.getSensors().put(sensor.getId(), sensor);
        DataStore.getRooms().get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        DataStore.getReadings().put(sensor.getId(), new ArrayList<>());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // Links to the readings for a specific sensor
    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}