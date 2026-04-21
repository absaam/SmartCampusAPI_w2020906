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

    // GET /api/v1/sensors - get all sensors (with optional type filter)
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(DataStore.getSensors().values());
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor s : sensorList) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filtered.add(s);
                }
            }
            return Response.ok(filtered).build();
        }
        return Response.ok(sensorList).build();
    }

    // GET /api/v1/sensors/{sensorId} - get a specific sensor
    @GET
    @Path("{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }

    // POST /api/v1/sensors - create a new sensor
    @POST
    public Response createSensor(Sensor sensor) {
        if (!DataStore.getRooms().containsKey(sensor.getRoomId())) {
            return Response.status(422)
                    .entity("{\"error\":\"Room not found for given roomId\"}")
                    .build();
        }
        if (DataStore.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Sensor already exists\"}")
                    .build();
        }
        DataStore.getSensors().put(sensor.getId(), sensor);
        DataStore.getRooms().get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        DataStore.getReadings().put(sensor.getId(), new ArrayList<>());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // Sub-resource locator for readings
    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}