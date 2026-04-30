package Vista;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Vista para mostrar precios con descuento.
 */
public class ConsoleDiscountView {
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public void mostrarPrecioConDescuento(String nombreProducto, BigDecimal original, BigDecimal conDescuento) {
        System.out.println("Producto: " + nombreProducto);
        System.out.println("Precio original: " + df.format(original.doubleValue()));
        System.out.println("Precio con descuento: " + df.format(conDescuento.doubleValue()));
    }
}
