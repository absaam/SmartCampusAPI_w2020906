package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// DataStore acts as the in-memory database for the Smart Campus API
public class DataStore {

    // Maps to store rooms, sensors and readings using their IDs as keys
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // Sample data loaded when the app starts
    static {
        Room r1 = new Room("WEST-101", "Westminster Lecture Hall", 120);
        Room r2 = new Room("WEST-202", "Engineering Computer Lab", 45);
        Room r3 = new Room("WEST-303", "Student Study Lounge", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        Sensor s1 = new Sensor("TEMP-W01", "Temperature", "ACTIVE", 21.3, "WEST-101");
        Sensor s2 = new Sensor("CO2-W01", "CO2", "ACTIVE", 412.0, "WEST-202");
        Sensor s3 = new Sensor("OCC-W01", "Occupancy", "ACTIVE", 35.0, "WEST-303");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);

        // Link sensors to their rooms
        r1.getSensorIds().add(s1.getId());
        r2.getSensorIds().add(s2.getId());
        r3.getSensorIds().add(s3.getId());

        readings.put(s1.getId(), new ArrayList<>());
        readings.put(s2.getId(), new ArrayList<>());
        readings.put(s3.getId(), new ArrayList<>());
    }

    // Getter methods to access the data stores
    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Map<String, List<SensorReading>> getReadings() {
        return readings;
    }
}