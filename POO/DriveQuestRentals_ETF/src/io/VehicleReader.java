/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.CargoVehicle;
import model.PassengerVehicle;
import model.Vehicle;

/**
 *
 * @author Felip
 */
public class VehicleReader {
    public static List<Vehicle> load(String path) {
        List<Vehicle> list = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] f = line.split(",");
                try {
                    String type = f[0];
                    String plate = f[1], brand = f[2], model = f[3];
                    int days = Integer.parseInt(f[4]);
                    if ("C".equalsIgnoreCase(type)) {
                        double cap = Double.parseDouble(f[5]);
                        list.add(new CargoVehicle(plate, brand, model, days, cap));
                    } else {
                        int pax = Integer.parseInt(f[5]);
                        list.add(new PassengerVehicle(plate, brand, model, days, pax));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    System.err.println("Formato inválido en línea: '" + line + "'. Omitida.");
                }
            }
        } catch (IOException ex) {
            System.err.println("Error leyendo archivo " + path + ": " + ex.getMessage());
        }
        return list;
    }
}
