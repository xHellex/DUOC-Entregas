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
    private final String categoria;

    // Constructor con categoría (recomendado)
    public Producto(String nombre, double precio, String categoria) {
        this(nombre, BigDecimal.valueOf(precio), categoria);
    }

    // Constructor sin categoría -> usa "GENERAL"
    public Producto(String nombre, double precio) {
        this(nombre, BigDecimal.valueOf(precio), "GENERAL");
    }

    public Producto(String nombre, BigDecimal precio, String categoria) {
        if (precio == null) throw new IllegalArgumentException("Precio no puede ser nulo.");
        if (precio.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Precio no puede ser negativo.");
        if (categoria == null) categoria = "GENERAL";
        this.nombre = nombre;
        this.precio  = precio.setScale(2, java.math.RoundingMode.HALF_UP);
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
}