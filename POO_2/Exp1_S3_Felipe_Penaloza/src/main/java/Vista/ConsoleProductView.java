package Vista;

import Producto.Producto;
import java.util.List;

/**
 * Vista de consola simple para mostrar catálogo.
 */
public class ConsoleProductView {

    public void mostrarCatalogo(List<Producto> productos) {
        System.out.println("=== Catálogo ===");
        for (Producto p : productos) {
            System.out.printf("%s - %s : %,.2f (%s)%n",
                    p.getSku(), p.getNombre(), p.getPrecio().doubleValue(), p.getCategoria());
        }
    }
}
