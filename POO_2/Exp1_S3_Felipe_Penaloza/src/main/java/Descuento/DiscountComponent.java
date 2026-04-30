package Descuento;

import Producto.Producto;
import java.math.BigDecimal;

/**
 * Interfaz del componente para Decorator: aplica una transformación (descuento) al precio.
 */
public interface DiscountComponent {
    BigDecimal apply(BigDecimal precioActual, Producto producto);
}
