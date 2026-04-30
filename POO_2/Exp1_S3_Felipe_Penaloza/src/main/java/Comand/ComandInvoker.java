package Comand;

import Producto.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Invocador simple de comandos.
 */
public class ComandInvoker {
    private final List<Comand> lista = new ArrayList<>();

    public void addComand(Comand c) {
        if (c == null) throw new IllegalArgumentException("Comand null");
        lista.add(c);
    }

    public BigDecimal executeAll(Producto producto) {
        BigDecimal ultimo = producto.getPrecio();
        for (Comand c : lista) {
            ultimo = c.ejecutar(producto);
        }
        return ultimo;
    }

    public void clear() {
        lista.clear();
    }
}
