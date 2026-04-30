package Producto;
import java.math.BigDecimal;

/**
 * Producto con sku, nombre, precio (BigDecimal) y categoría.
 */
public class Producto {
    private final String sku;
    private final String nombre;
    private final BigDecimal precio;
    private final String categoria;

    public Producto(String sku, String nombre, double precio, String categoria) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU requerido");
        this.sku = sku;
        this.nombre = nombre == null ? "" : nombre;
        this.precio = BigDecimal.valueOf(precio).setScale(2, java.math.RoundingMode.HALF_UP);
        this.categoria = categoria == null ? "GENERAL" : categoria;
    }

    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
}
