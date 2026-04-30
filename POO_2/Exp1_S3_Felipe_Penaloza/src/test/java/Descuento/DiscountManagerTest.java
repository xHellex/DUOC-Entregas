package Descuento;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountManagerTest {

    private DiscountManager dm;

    @BeforeEach
    public void setUp() {
        dm = DiscountManager.getInstance();
        dm.setDescuentoGlobal(0.0);
        dm.clearDecorators();
    }

    @Test
    public void testSingletonSameInstance() {
        DiscountManager a = DiscountManager.getInstance();
        DiscountManager b = DiscountManager.getInstance();
        assertSame(a, b, "getInstance() debe devolver la misma referencia (singleton).");
    }

    @Test
    public void testSetDescuentoGlobalInvalidLow() {
        assertThrows(IllegalArgumentException.class, () -> dm.setDescuentoGlobal(-0.01));
    }

    @Test
    public void testSetDescuentoGlobalInvalidHigh() {
        assertThrows(IllegalArgumentException.class, () -> dm.setDescuentoGlobal(150.0));
    }

    @Test
    public void testAplicarDescuentoNegativePrice() {
        BigDecimal negativo = BigDecimal.valueOf(-100.00);
        assertThrows(IllegalArgumentException.class, () -> dm.aplicarDescuento(negativo));
    }

    @Test
    public void testAplicarDescuentoCalculation() {
        dm.setDescuentoGlobal(15.0); // 15%
        BigDecimal precio = BigDecimal.valueOf(19990.00);
        BigDecimal expected = precio.multiply(BigDecimal.valueOf(0.85)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = dm.aplicarDescuento(precio);
        assertEquals(0, expected.compareTo(actual), "El precio con descuento debe coincidir (2 decimales).");
    }
}
