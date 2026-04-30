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

/**
 * Comando que aplica la cadena de decoradores registrada en el DiscountManager.
 */
public class ApplyDecoratorCommand implements DiscountCommand {

    @Override
    public BigDecimal execute(Producto producto) {
        DiscountManager dm = DiscountManager.getInstance();
        return dm.applyDecoratorsToProduct(producto);
    }
}