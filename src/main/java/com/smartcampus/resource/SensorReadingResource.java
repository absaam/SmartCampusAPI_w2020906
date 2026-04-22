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

    // Get all readings for this sensor
    @GET
    public Response getReadings() {
        Sensor foundSensor = DataStore.getSensors().get(sensorId);
        if (foundSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No sensor found with that ID\"}")
                    .build();
        }
        List<SensorReading> sensorReadings = DataStore.getReadings()
                .getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(sensorReadings).build();
    }

    // Add a new reading - blocked if sensor is under maintenance
    @POST
    public Response addReading(SensorReading reading) {
        Sensor foundSensor = DataStore.getSensors().get(sensorId);
        if (foundSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No sensor found with that ID\"}")
                    .build();
        }
        if (foundSensor.getStatus().equalsIgnoreCase("MAINTENANCE")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\":\"This sensor is currently under maintenance and cannot record readings\"}")
                    .build();
        }
        // Save reading and update the sensor's current value
        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.getReadings().get(sensorId).add(newReading);
        foundSensor.setCurrentValue(reading.getValue());
        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}