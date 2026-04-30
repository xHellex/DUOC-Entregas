package Descuento;

import Producto.Producto;
import java.math.BigDecimal;

/**
 * Componente base: devuelve el precio sin cambios.
 */
public class BasePrice implements DiscountComponent {
    @Override
    public BigDecimal apply(BigDecimal precioActual, Producto producto) {
        return precioActual;
    }
}
