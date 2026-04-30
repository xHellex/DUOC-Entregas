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
 * Interfaz del componente para Decorator: aplica una transformación (descuento) al precio.
 */
public interface DiscountComponent {
    /**
     * Aplica la lógica del componente (o decorador) sobre el precio y devuelve el nuevo precio.
     * @param precioActual precio actual (resultado del componente interno)
     * @param producto contexto del producto (puede usarse por decoradores basados en categoría)
     * @return precio modificado por este componente
     */
    BigDecimal apply(BigDecimal precioActual, Producto producto);
}