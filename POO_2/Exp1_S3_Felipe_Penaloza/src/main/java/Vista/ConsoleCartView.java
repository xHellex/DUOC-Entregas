package Vista;

import Modelo.Pedido;
import java.text.DecimalFormat;

/**
 * Vista de consola para mostrar carrito/pedido.
 */
public class ConsoleCartView {
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public void mostrarPedido(Pedido pedido) {
        System.out.println("=== Pedido " + pedido.getId() + " (usuario " + pedido.getUsuarioId() + ") ===");
        pedido.getItems().forEach(ci -> {
            System.out.printf("%s x%d -> %s%n",
                    ci.getProducto().getNombre(),
                    ci.getCantidad(),
                    df.format(ci.getTotal().doubleValue()));
        });
        System.out.println("Subtotal: " + df.format(pedido.calcularSubtotal().doubleValue()));
    }
}
