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
import java.math.RoundingMode;

/**
 * Aplica un descuento porcentual sobre el resultado del componente interior.
 */
public class PercentageDiscountDecorator extends DiscountDecorator {

    private final BigDecimal porcentaje; // ej: 15.0 = 15%

    public PercentageDiscountDecorator(DiscountComponent inner, double porcentaje) {
        super(inner);
        this.porcentaje = BigDecimal.valueOf(porcentaje);
        if (this.porcentaje.compareTo(BigDecimal.ZERO) < 0 || this.porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Porcentaje fuera de rango 0-100");
        }
    }

    @Override
    public BigDecimal apply(BigDecimal precioActual, Producto producto) {
        BigDecimal base = inner.apply(precioActual, producto);
        BigDecimal cien = BigDecimal.valueOf(100);
        BigDecimal factor = cien.subtract(porcentaje).divide(cien, 10, RoundingMode.HALF_UP);
        return base.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}