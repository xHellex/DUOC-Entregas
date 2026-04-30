package Modelo;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import Producto.Producto;

/**
 * Pedido (cart) con operaciones básicas
 */
public class Pedido {
    private final String id;
    private final String usuarioId;
    private final List<CartItem> items = new ArrayList<>();

    public Pedido(String id, String usuarioId) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id requerido");
        if (usuarioId == null || usuarioId.isBlank()) throw new IllegalArgumentException("usuarioId requerido");
        this.id = id;
        this.usuarioId = usuarioId;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public List<CartItem> getItems() { return items; }

    public void agregarProducto(Producto p, int cantidad) {
        if (p == null) throw new IllegalArgumentException("Producto null");
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad inválida");
        for (CartItem ci : items) {
            if (ci.getProducto().getSku().equals(p.getSku())) {
                ci.setCantidad(ci.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new CartItem(p, cantidad));
    }

    public void eliminarProducto(String sku) {
        items.removeIf(ci -> ci.getProducto().getSku().equals(sku));
    }

    public BigDecimal calcularSubtotal() {
        return items.stream()
                    .map(CartItem::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
