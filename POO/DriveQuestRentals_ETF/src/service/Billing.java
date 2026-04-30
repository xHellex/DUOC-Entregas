/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.CargoVehicle;
import model.PassengerVehicle;
import model.Vehicle;

/**
 *
 * @author Felip
 */
public interface Billing {
    double IVA = 0.19;
    double DISCOUNT_CARGO = 0.07;
    double DISCOUNT_PASSENGER = 0.12;

    default double calculate(Vehicle v, double dailyRate) {
        double subtotal = dailyRate * v.getRentDays();
        double ivaAmount = subtotal * IVA;
        double discount = 0;
        if (v instanceof CargoVehicle) discount = subtotal * DISCOUNT_CARGO;
        else if (v instanceof PassengerVehicle) discount = subtotal * DISCOUNT_PASSENGER;
        return subtotal + ivaAmount - discount;
    }
}