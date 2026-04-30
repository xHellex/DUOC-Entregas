package Inventario;
import Producto.Producto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Inventario de productos.
 */
public class Inventario implements InventarioRepository {
    private final List<Producto> productos;

    public Inventario() {
        this.productos = new ArrayList<>();
    }

    @Override
    public List<Producto> listarTodos() {
        return Collections.unmodifiableList(productos);
    }

    @Override
    public Producto buscarPorId(String id) {
        if (id == null) return null;
        for (Producto p : productos) {
            if (p.getId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Producto buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void agregar(Producto p) {
        if (p == null) throw new IllegalArgumentException("No se puede agregar un producto nulo.");
        if (buscarPorId(p.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con el ID: " + p.getId());
        }
        productos.add(p);
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null) return false;
        Producto encontrado = buscarPorId(id);
        if (encontrado == null) return false;
        return productos.remove(encontrado);
    }

    @Override
    public boolean existeId(String id) {
        return buscarPorId(id) != null;
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public String generarInforme() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Informe de Inventario ===\n");
        if (productos.isEmpty()) {
            sb.append("No hay productos registrados.\n");
        } else {
            for (Producto p : productos) {
                sb.append(p.toString()).append('\n');
            }
            int totalUnidades = productos.stream().mapToInt(Producto::getCantidad).sum();
            double totalValorizado = productos.stream().mapToDouble(Producto::calcularTotal).sum();
            sb.append("Unidades totales: ").append(totalUnidades).append('\n');
            sb.append("Valor total (precio*cantidad): ").append(totalValorizado).append('\n');
        }
        return sb.toString();
    }
}