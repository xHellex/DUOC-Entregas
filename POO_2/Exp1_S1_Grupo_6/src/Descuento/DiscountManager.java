/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Descuento;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 *
 * @author Felipe Peñaloza Oyarzún & Joaquín Gómez Flores
 */
/**
 * DiscountManager implementado como singleton eager (instancia privada, estática y final).
 * Usa BigDecimal para manejo de dinero y porcentajes.
 */
public class DiscountManager {

    // Instancia única, privada, estática y final (cumple pauta)
    private static final DiscountManager INSTANCIA = new DiscountManager();

    // Porcentaje entre 0.0 y 100.0 (ej: 15.0 = 15%)
    // Almacenado como BigDecimal para mayor precisión.
    private BigDecimal descuentoGlobal;

    // Constructor privado
    private DiscountManager() {
        this.descuentoGlobal = BigDecimal.ZERO;
    }

    // Punto de acceso público
    public static DiscountManager getInstance() {
        return INSTANCIA;
    }

    // Setter sincronizado para mayor seguridad en entornos concurrentes
    public synchronized void setDescuentoGlobal(double porcentaje) {
        BigDecimal bd = BigDecimal.valueOf(porcentaje);
        if (bd.compareTo(BigDecimal.ZERO) < 0 || bd.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Descuento fuera de rango (0 a 100).");
        }
        this.descuentoGlobal = bd;
    }

    // Devuelve el porcentaje actual (BigDecimal)
    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }

    /**
     * Aplica el descuento al precio original y devuelve el precio con 2 decimales, redondeado HALF_UP.
     * Lanza IllegalArgumentException si precioOriginal es null o negativo.
     */
    public BigDecimal aplicarDescuento(BigDecimal precioOriginal) {
        if (precioOriginal == null) {
            throw new IllegalArgumentException("Precio no puede ser nulo.");
        }
        if (precioOriginal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo.");
        }

        BigDecimal cien = BigDecimal.valueOf(100);
        BigDecimal factor = cien.subtract(descuentoGlobal)
                               .divide(cien, 10, RoundingMode.HALF_UP); // precisión interna
        BigDecimal resultado = precioOriginal.multiply(factor)
                                             .setScale(2, RoundingMode.HALF_UP);
        return resultado;
    }

    // Alternativa de conveniencia: aceptar double como precio
    public BigDecimal aplicarDescuento(double precioOriginal) {
        return aplicarDescuento(BigDecimal.valueOf(precioOriginal));
    }
}
