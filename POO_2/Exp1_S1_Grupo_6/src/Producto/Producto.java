/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Producto;
import java.math.BigDecimal;
/**
 *
 * @author Felipe Peñaloza Oyarzún & Joaquín Gómez Flores
 */
public class Producto {
    private final String nombre;
    private final BigDecimal precio;

    public Producto(String nombre, double precio) {
        this(nombre, BigDecimal.valueOf(precio));
    }

    public Producto(String nombre, BigDecimal precio) {
        if (precio == null) throw new IllegalArgumentException("Precio no puede ser nulo.");
        if (precio.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Precio no puede ser negativo.");
        this.nombre = nombre;
        this.precio  = precio.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
}