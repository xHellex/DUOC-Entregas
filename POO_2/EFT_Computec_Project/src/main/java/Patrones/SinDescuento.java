/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patrones;

import java.math.BigDecimal;

public class SinDescuento implements Descuento {
    @Override
    public BigDecimal aplicar(BigDecimal precio) {
        return precio;
    }
}
