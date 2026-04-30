package Descuento;

import Producto.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Resta una cantidad fija (en misma unidad) al precio resultante del componente interior.
 */
public class FixedAmountDecorator extends DiscountDecorator {

    private final BigDecimal amount;

    public FixedAmountDecorator(DiscountComponent inner, double amount) {
        super(inner);
        this.amount = BigDecimal.valueOf(amount);
        if (this.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount debe ser >= 0");
        }
    }

    @Override
    public BigDecimal apply(BigDecimal precioActual, Producto producto) {
        BigDecimal base = inner.apply(precioActual, producto);
        BigDecimal result = base.subtract(amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) result = BigDecimal.ZERO;
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
