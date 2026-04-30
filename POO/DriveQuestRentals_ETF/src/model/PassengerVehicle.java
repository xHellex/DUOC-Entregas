/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Felip
 */
public class PassengerVehicle extends Vehicle {
    private int maxPassengers;

    public PassengerVehicle() { }

    public PassengerVehicle(String plate, String brand, String model, int rentDays, int maxPassengers) {
        super(plate, brand, model, rentDays);
        this.maxPassengers = maxPassengers;
    }

    public int getMaxPassengers() { return maxPassengers; }
    public void setMaxPassengers(int maxPassengers) { this.maxPassengers = maxPassengers; }

    @Override
    public String showInfo() {
        return String.format("Pasajeros [%s]: %s %s, días=%d, pax=%d",
            getPlate(), getBrand(), getModel(), getRentDays(), maxPassengers);
    }
}
