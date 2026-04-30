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
 * Interfaz Command para operaciones de descuento.
 */
public interface DiscountCommand {
    /**
     * Ejecuta la acción de descuento sobre el producto y devuelve el precio resultante.
     */
    BigDecimal execute(Producto producto);
}