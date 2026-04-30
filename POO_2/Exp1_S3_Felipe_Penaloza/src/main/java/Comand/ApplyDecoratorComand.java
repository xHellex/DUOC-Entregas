package Comand;

import Descuento.DiscountManager;
import Producto.Producto;
import java.math.BigDecimal;

public class ApplyDecoratorComand implements Comand {

    @Override
    public BigDecimal ejecutar(Producto producto) {
        DiscountManager dm = DiscountManager.getInstance();
        return dm.applyDecoratorsToProduct(producto);
    }
}
