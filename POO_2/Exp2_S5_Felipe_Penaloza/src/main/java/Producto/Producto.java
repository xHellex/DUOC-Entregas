package Producto;


import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Representación de producto con validaciones robustas.
 */
public final class Producto {
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,20}$");

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;

    public Producto(String id, String nombre, String descripcion, double precio, int cantidad) {
        setId(id);
        setNombre(nombre);
        setDescripcion(descripcion);
        setPrecio(precio);
        setCantidad(cantidad);
    }

    // Constructor alternativo (compatibilidad)
    public Producto(String codigo, String nombre, double precio) {
        this(codigo, nombre, "", precio, 0);
    }

    private void validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío.");
        }
    }

    public String getId() { return id; }
    public void setId(String id) {
        validarNoVacio(id, "ID");
        String trimmed = id.trim();
        if (!ID_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("ID inválido. Use 1-20 caracteres alfanuméricos, guiones o guion bajo.");
        }
        this.id = trimmed;
    }

    // Alias codigo <-> id
    public String getCodigo() { return getId(); }
    public void setCodigo(String codigo) { setId(codigo); }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { validarNoVacio(nombre, "Nombre"); this.nombre = nombre.trim(); }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = (descripcion == null) ? "" : descripcion.trim(); }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        this.cantidad = cantidad;
    }

    // Comportamientos
    public void actualizarStock(int nuevaCantidad) {
        setCantidad(nuevaCantidad);
    }

    public void actualizarPrecio(double nuevoPrecio) {
        setPrecio(nuevoPrecio);
    }

    public double calcularTotal() {
        return precio * cantidad;
    }

    public String descripcionDetallada() {
        return String.format("%s - %s (%s) Precio: %.2f Cantidad: %d", id, nombre, descripcion, precio, cantidad);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + String.format("%.2f", precio) +
                ", cantidad=" + cantidad +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
