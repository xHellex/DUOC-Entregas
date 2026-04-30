/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Descuento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import Producto.Producto;

/**
 * DiscountManager implementado como singleton eager (instancia privada, estática y final).
 * Mantiene:
 *  - descuentoGlobal (BigDecimal)
 *  - una cadena de decoradores (DiscountComponent) para promociones más flexibles (Decorator pattern)
 */
public class DiscountManager {

    // Instancia única, privada, estática y final (cumple pauta)
    private static final DiscountManager INSTANCIA = new DiscountManager();

    // Porcentaje entre 0.0 y 100.0 (ej: 15.0 = 15%)
    private BigDecimal descuentoGlobal;

    // Cadena de decoradores (por defecto BasePrice)
    private DiscountComponent discountChain;

    // Constructor privado
    private DiscountManager() {
        this.descuentoGlobal = BigDecimal.ZERO;
        this.discountChain = new BasePrice();
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

    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }

    /**
     * Aplica el descuento global al precioOriginal y devuelve precio con 2 decimales
     */
    public BigDecimal aplicarDescuento(BigDecimal precioOriginal) {
        if (precioOriginal == null) throw new IllegalArgumentException("Precio no puede ser nulo.");
        if (precioOriginal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Precio no puede ser negativo.");

        BigDecimal cien = BigDecimal.valueOf(100);
        BigDecimal factor = cien.subtract(descuentoGlobal).divide(cien, 10, RoundingMode.HALF_UP);
        BigDecimal resultado = precioOriginal.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return resultado;
    }

    public BigDecimal aplicarDescuento(double precioOriginal) {
        return aplicarDescuento(BigDecimal.valueOf(precioOriginal));
    }

    // --- Métodos para manejar la cadena de decoradores (Decorator pattern) ---

    /**
     * Añade un PercentageDiscountDecorator al final de la cadena:
     * discountChain = new PercentageDiscountDecorator(discountChain, porcentaje);
     */
    public synchronized void addPercentageDecorator(double porcentaje) {
        this.discountChain = new PercentageDiscountDecorator(this.discountChain, porcentaje);
    }

    /**
     * Añade un CategoryPercentageDiscountDecorator al final de la cadena.
     */
    public synchronized void addCategoryPercentageDecorator(double porcentaje, String categoria) {
        this.discountChain = new CategoryPercentageDiscountDecorator(this.discountChain, porcentaje, categoria);
    }

    /**
     * Añade un FixedAmountDecorator al final de la cadena.
     */
    public synchronized void addFixedAmountDecorator(double amount) {
        this.discountChain = new FixedAmountDecorator(this.discountChain, amount);
    }

    /**
     * Limpia la cadena de decoradores (vuelve a BasePrice).
     */
    public synchronized void clearDecorators() {
        this.discountChain = new BasePrice();
    }

    /**
     * Aplica la cadena de decoradores a un producto y devuelve el precio resultante.
     */
    public BigDecimal applyDecoratorsToProduct(Producto producto) {
        if (producto == null) throw new IllegalArgumentException("Producto no puede ser nulo.");
        // llamamos al componente base con el precio original
        return discountChain.apply(producto.getPrecio(), producto);
    }
}