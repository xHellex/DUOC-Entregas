package Descuento;

import Producto.Producto;
import java.math.BigDecimal;

/**
 * Decorador abstracto que envuelve otro DiscountComponent.
 */
public abstract class DiscountDecorator implements DiscountComponent {
    protected final DiscountComponent inner;

    public DiscountDecorator(DiscountComponent inner) {
        if (inner == null) throw new IllegalArgumentException("Inner component no puede ser null.");
        this.inner = inner;
    }

    @Override
    public abstract BigDecimal apply(BigDecimal precioActual, Producto producto);
}
