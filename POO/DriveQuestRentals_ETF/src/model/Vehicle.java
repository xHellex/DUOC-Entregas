/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Felip
 */
public abstract class Vehicle {
    private String plate;
    private String brand;
    private String model;
    private int rentDays;

    public Vehicle() { }

    public Vehicle(String plate, String brand, String model, int rentDays) {
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.rentDays = rentDays;
    }

    // Getters y Setters
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getRentDays() { return rentDays; }
    public void setRentDays(int rentDays) { this.rentDays = rentDays; }

    public abstract String showInfo();
}