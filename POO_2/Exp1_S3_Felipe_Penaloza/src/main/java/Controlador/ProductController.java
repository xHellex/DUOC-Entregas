package Controlador;

import Producto.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador simple que gestiona el catálogo de productos.
 */
public class ProductController {
    private final List<Producto> catalogo = new ArrayList<>();

    public void agregarProducto(Producto p) {
        if (p == null) throw new IllegalArgumentException("Producto null");
        catalogo.add(p);
    }

    public List<Producto> listarProductos() {
        return List.copyOf(catalogo);
    }

    public Producto buscarPorSku(String sku) {
        return catalogo.stream()
                .filter(p -> p.getSku().equals(sku))
                .findFirst()
                .orElse(null);
    }
}
