package Modelo;

import Producto.Producto;
import java.math.BigDecimal;

/**
 * Item del carrito: producto + cantidad
 */
public class CartItem {
    private final Producto producto;
    private int cantidad;

    public CartItem(Producto producto, int cantidad) {
        if (producto == null) throw new IllegalArgumentException("Producto no puede ser null");
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser > 0");
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser > 0");
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }
}
