package Comand;

import Descuento.DiscountManager;
import Producto.Producto;
import java.math.BigDecimal;

public class ApplyGlobalDiscountComand implements Comand {

    @Override
    public BigDecimal ejecutar(Producto producto) {
        DiscountManager dm = DiscountManager.getInstance();
        return dm.aplicarDescuento(producto.getPrecio());
    }
}
