/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;

public final class Laptop extends Equipo {
    private double pantallaPulgadas;
    private boolean touch;
    private int puertosUsb;

    public Laptop(String modelo, String cpu, int discoMb, int ramGb, BigDecimal precio,
                  double pantallaPulgadas, boolean touch, int puertosUsb) {
        super(modelo, cpu, discoMb, ramGb, precio, Tipo.LAPTOP);
        setPantallaPulgadas(pantallaPulgadas);
        setTouch(touch);
        setPuertosUsb(puertosUsb);
    }

    public double getPantallaPulgadas() { return pantallaPulgadas; }
    public void setPantallaPulgadas(double pantallaPulgadas) {
        if (pantallaPulgadas <= 0) throw new IllegalArgumentException("Tamaño de pantalla inválido");
        this.pantallaPulgadas = pantallaPulgadas;
    }

    public boolean isTouch() { return touch; }
    public void setTouch(boolean touch) { this.touch = touch; }

    public int getPuertosUsb() { return puertosUsb; }
    public void setPuertosUsb(int puertosUsb) {
        if (puertosUsb < 0) throw new IllegalArgumentException("Puertos USB inválidos");
        this.puertosUsb = puertosUsb;
    }
}
