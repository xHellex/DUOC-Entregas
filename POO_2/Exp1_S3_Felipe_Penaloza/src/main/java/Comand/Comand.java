package Comand;

import Producto.Producto;
import java.math.BigDecimal;

/**
 * Interfaz Comand con método ejecutar (según pauta: 'Comand' y 'ejecutar')
 */
public interface Comand {
    BigDecimal ejecutar(Producto producto);
}
