package Descuento;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Producto.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountCompositionTest {

    private DiscountManager dm;

    @BeforeEach
    public void setup() {
        dm = DiscountManager.getInstance();
        dm.setDescuentoGlobal(0.0);
        dm.clearDecorators();
    }

    @Test
    public void decoratorsComposeCorrectly_orderAndEffect() {
        Producto p = new Producto("TEST-01", "Producto Test", 100.00, "ROPA");

        // cadena: 10% general -> 90.00 ; luego 20% en ROPA -> 72.00 ; luego -5 (fixed) -> 67.00
        dm.addPercentageDecorator(10.0);
        dm.addCategoryPercentageDecorator(20.0, "ROPA");
        dm.addFixedAmountDecorator(5.0);

        BigDecimal result = dm.applyDecoratorsToProduct(p);

        BigDecimal expected = BigDecimal.valueOf(67.00).setScale(2, RoundingMode.HALF_UP);
        assertEquals(0, expected.compareTo(result));
    }

    @Test
    public void decoratorsDoNotProduceNegativePrice() {
        Producto p = new Producto("TEST-02", "Producto Barato", 2.00, "GENERAL");

        // Fixed amount higher que el precio debe terminar en 0.00
        dm.addFixedAmountDecorator(5.0);
        BigDecimal result = dm.applyDecoratorsToProduct(p);
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(result));
    }
}
