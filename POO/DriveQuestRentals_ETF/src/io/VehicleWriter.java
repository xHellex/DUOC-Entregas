/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import model.CargoVehicle;
import model.PassengerVehicle;
import model.Vehicle;

/**
 *
 * @author Felip
 */
public class VehicleWriter {
    public static void save(String path, Vehicle v) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            String type = v instanceof CargoVehicle ? "C" : "P";
            String extra = v instanceof CargoVehicle
                ? String.valueOf(((CargoVehicle)v).getLoadCapacity())
                : String.valueOf(((PassengerVehicle)v).getMaxPassengers());
            String line = String.join(",",
                type, v.getPlate(), v.getBrand(), v.getModel(),
                String.valueOf(v.getRentDays()), extra);
            bw.write(line);
            bw.newLine();
        } catch (IOException ex) {
            System.err.println("Error escribiendo en archivo " + path + ": " + ex.getMessage());
        }
    }
}
