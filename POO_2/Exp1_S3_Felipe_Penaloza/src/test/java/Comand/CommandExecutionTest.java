package Comand;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Descuento.DiscountManager;
import Producto.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommandExecutionTest {

    private DiscountManager dm;

    @BeforeEach
    public void setup() {
        dm = DiscountManager.getInstance();
        dm.setDescuentoGlobal(0.0);
        dm.clearDecorators();
    }

    @Test
    public void invokerExecutesCommandsAndReturnsLastResult() {
        Producto p = new Producto("CMD-01", "Test Command", 200.00, "ROPA");
        dm.setDescuentoGlobal(10.0); // 200 -> 180
        dm.addPercentageDecorator(20.0); // 200 -> 160

        ComandInvoker inv = new ComandInvoker();
        inv.addComand(new ApplyGlobalDiscountComand());   // returns 180
        inv.addComand(new ApplyDecoratorComand());        // returns 160 (decorators applied to original by design)

        java.math.BigDecimal last = inv.executeAll(p);

        java.math.BigDecimal expected = java.math.BigDecimal.valueOf(160.00).setScale(2, RoundingMode.HALF_UP);
        assertEquals(0, expected.compareTo(last));
    }
}
