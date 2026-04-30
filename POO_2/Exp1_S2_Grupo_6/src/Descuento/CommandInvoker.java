/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Descuento;

/**
 *
 * @author Felip
 */
import Producto.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Invocador que mantiene una lista de comandos y los ejecuta en orden.
 */
public class CommandInvoker {

    private final List<DiscountCommand> commands = new ArrayList<>();

    public void addCommand(DiscountCommand cmd) {
        if (cmd == null) throw new IllegalArgumentException("Command no puede ser null.");
        commands.add(cmd);
    }

    /**
     * Ejecuta todos los comandos sobre el producto en orden.
     * Devuelve el último precio obtenido (resultado del último comando).
     */
    public BigDecimal executeAll(Producto producto) {
        BigDecimal last = producto.getPrecio();
        for (DiscountCommand c : commands) {
            last = c.execute(producto);
        }
        return last;
    }

    public void clearCommands() {
        commands.clear();
    }
}