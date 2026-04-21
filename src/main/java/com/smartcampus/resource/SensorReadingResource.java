package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.storage.DataStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings - get all readings for a sensor
    @GET
    public Response getReadings() {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found\"}")
                    .build();
        }
        List<SensorReading> readings = DataStore.getReadings()
                .getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings - add a new reading
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found\"}")
                    .build();
        }
        if (sensor.getStatus().equalsIgnoreCase("MAINTENANCE")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\":\"Sensor is under maintenance and cannot accept readings\"}")
                    .build();
        }
        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.getReadings().get(sensorId).add(newReading);
        sensor.setCurrentValue(reading.getValue());
        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}