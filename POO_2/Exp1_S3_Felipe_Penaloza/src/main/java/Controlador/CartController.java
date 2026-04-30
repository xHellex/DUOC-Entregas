package Controlador;

import Modelo.Pedido;
import Producto.Producto;
import Descuento.DiscountManager;
import java.math.BigDecimal;

/**
 * Controlador que maneja las operaciones del carrito/pedido.
 */
public class CartController {

    private final Pedido pedido;
    private final DiscountManager dm = DiscountManager.getInstance();

    public CartController(Pedido pedido) {
        if (pedido == null) throw new IllegalArgumentException("Pedido null");
        this.pedido = pedido;
    }

    public void agregarProducto(Producto p, int cantidad) {
        pedido.agregarProducto(p, cantidad);
    }

    public void eliminarProducto(String sku) {
        pedido.eliminarProducto(sku);
    }

    public BigDecimal obtenerSubtotal() {
        return pedido.calcularSubtotal();
    }

    // Aplicar descuento global al subtotal y devolver total
    public BigDecimal aplicarDescuentoGlobalAlSubtotal() {
        BigDecimal subtotal = obtenerSubtotal();
        return dm.aplicarDescuento(subtotal);
    }

    // Aplicar decoradores producto a producto y sumar totales
    public BigDecimal aplicarDecoradoresYSumar() {
        java.math.BigDecimal total = BigDecimal.ZERO;
        for (var item : pedido.getItems()) {
            java.math.BigDecimal precioConDesc = dm.applyDecoratorsToProduct(item.getProducto());
            java.math.BigDecimal linea = precioConDesc.multiply(java.math.BigDecimal.valueOf(item.getCantidad()));
            total = total.add(linea);
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public Pedido getPedido() { return pedido; }
}
