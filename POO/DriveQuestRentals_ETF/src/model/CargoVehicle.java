/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Felip
 */
public class CargoVehicle extends Vehicle {
    private double loadCapacity;

    public CargoVehicle() { }

    public CargoVehicle(String plate, String brand, String model, int rentDays, double loadCapacity) {
        super(plate, brand, model, rentDays);
        this.loadCapacity = loadCapacity;
    }

    public double getLoadCapacity() { return loadCapacity; }
    public void setLoadCapacity(double loadCapacity) { this.loadCapacity = loadCapacity; }

    @Override
    public String showInfo() {
        return String.format("Carga [%s]: %s %s, días=%d, cap=%.1f t",
            getPlate(), getBrand(), getModel(), getRentDays(), loadCapacity);
    }
}