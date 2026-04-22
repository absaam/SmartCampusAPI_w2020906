package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.storage.DataStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // Get all rooms
    @GET
    public Response getAllRooms() {
        List<Room> allRooms = new ArrayList<>(DataStore.getRooms().values());
        return Response.ok(allRooms).build();
    }

    // Get one room by ID
    @GET
    @Path("{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room foundRoom = DataStore.getRooms().get(roomId);
        if (foundRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No room found with that ID\"}")
                    .build();
        }
        return Response.ok(foundRoom).build();
    }

    // Add a new room
    @POST
    public Response createRoom(Room room) {
        if (DataStore.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"A room with this ID already exists\"}")
                    .build();
        }
        DataStore.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // Delete a room - blocked if the room still has sensors
    @DELETE
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room foundRoom = DataStore.getRooms().get(roomId);
        if (foundRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No room found with that ID\"}")
                    .build();
        }
        if (!foundRoom.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Cannot delete room as it still has sensors inside\"}")
                    .build();
        }
        DataStore.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}