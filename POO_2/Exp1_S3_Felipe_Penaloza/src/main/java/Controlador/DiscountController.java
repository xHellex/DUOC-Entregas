package Controlador;

import Descuento.*;
import java.math.BigDecimal;

/**
 * Controlador que permite configurar descuentos (decorators) y ejecutar comandos.
 */
public class DiscountController {

    private final DiscountManager dm = DiscountManager.getInstance();
    private final Comand.ComandInvoker invoker = new Comand.ComandInvoker();

    public void setDescuentoGlobal(double porcentaje) {
        dm.setDescuentoGlobal(porcentaje);
    }

    public BigDecimal getDescuentoGlobal() {
        return dm.getDescuentoGlobal();
    }

    public void addPercentageDecorator(double porcentaje) {
        dm.addPercentageDecorator(porcentaje);
    }

    public void addCategoryDecorator(double porcentaje, String categoria) {
        dm.addCategoryPercentageDecorator(porcentaje, categoria);
    }

    public void addFixedAmountDecorator(double amount) {
        dm.addFixedAmountDecorator(amount);
    }

    public void clearDecorators() {
        dm.clearDecorators();
    }

    // Comandos: los registramos y ejecutamos
    public void registrarComand(Comand.Comand c) {
        invoker.addComand(c);
    }

    public java.math.BigDecimal ejecutarTodos(Producto.Producto producto) {
        return invoker.executeAll(producto);
    }
}
