package Descuento;

import Producto.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Aplica un porcentaje solo si el producto pertenece a cierta categoría.
 */
public class CategoryPercentageDiscountDecorator extends DiscountDecorator {

    private final BigDecimal porcentaje;
    private final String categoria;

    public CategoryPercentageDiscountDecorator(DiscountComponent inner, double porcentaje, String categoria) {
        super(inner);
        if (categoria == null) categoria = "GENERAL";
        this.categoria = categoria;
        this.porcentaje = BigDecimal.valueOf(porcentaje);
        if (this.porcentaje.compareTo(BigDecimal.ZERO) < 0 || this.porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Porcentaje fuera de rango 0-100");
        }
    }

    @Override
    public BigDecimal apply(BigDecimal precioActual, Producto producto) {
        BigDecimal base = inner.apply(precioActual, producto);
        if (producto != null && categoria.equalsIgnoreCase(producto.getCategoria())) {
            BigDecimal cien = BigDecimal.valueOf(100);
            BigDecimal factor = cien.subtract(porcentaje).divide(cien, 10, RoundingMode.HALF_UP);
            return base.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        } else {
            return base; // no aplica
        }
    }
}
