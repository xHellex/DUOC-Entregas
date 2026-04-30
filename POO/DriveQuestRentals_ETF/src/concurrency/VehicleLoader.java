/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrency;

import io.VehicleReader;
import model.Vehicle;
import service.FleetManager;

/**
 *
 * @author Felip
 */
public class VehicleLoader extends Thread {
    private final String file;
    private final FleetManager mgr;

    public VehicleLoader(String file, FleetManager mgr) {
        this.file = file;
        this.mgr = mgr;
    }

    @Override
    public void run() {
        for (Vehicle v : VehicleReader.load(file)) {
            try {
                mgr.addVehicle(v);
            } catch (IllegalArgumentException ex) {
                System.err.println("Duplicado en carga: " + v.getPlate());
            }
        }
    }
}