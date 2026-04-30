/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Descuento;

/**
 *
 * @author Felip
 */


import Producto.Producto;
import java.math.BigDecimal;

/**
 * Componente base: devuelve el precio sin cambios.
 */
public class BasePrice implements DiscountComponent {
    @Override
    public BigDecimal apply(BigDecimal precioActual, Producto producto) {
        // precioActual suele ser el precio original del producto (o BigDecimal.ZERO si se usa de otra forma).
        return precioActual;
    }
}