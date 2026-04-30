/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Vehicle;

/**
 *
 * @author Felip
 */
public class FleetManager implements Billing {
    private final Map<String, Vehicle> registry = Collections.synchronizedMap(new HashMap<>());

    public void addVehicle(Vehicle v) {
        String plate = v.getPlate().toUpperCase();
        synchronized (registry) {
            if (registry.containsKey(plate)) {
                throw new IllegalArgumentException("Patente duplicada: " + plate);
            }
            registry.put(plate, v);
        }
    }

    public List<Vehicle> listAll() {
        synchronized (registry) {
            return new ArrayList<>(registry.values());
        }
    }

    public List<Vehicle> longTerm() {
        synchronized (registry) {
            List<Vehicle> result = new ArrayList<>();
            for (Vehicle v : registry.values()) {
                if (v.getRentDays() >= 7) result.add(v);
            }
            return result;
        }
    }
}