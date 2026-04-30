/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package App;

import Descuento.DiscountManager;
import Producto.Producto;
import Singleton.Singleton;

import java.text.DecimalFormat;
import java.math.BigDecimal;
/**
 *
 * @author Felipe Peñaloza Oyarzún & Joaquín Gómez Flores
 */
public class TiendaDeRopa {
    public static void main(String[] args) {
        // 1) Demostración literal del requerimiento: clase "Singleton"
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println("¿Singleton es la misma instancia? " + (s1 == s2)); // true

        // 2) Caso de negocio: gestor de descuentos centralizado
        DiscountManager dm1 = DiscountManager.getInstance();
        dm1.setDescuentoGlobal(15.0); // 15%

        // En "otro módulo" pedimos la instancia (debe ser la misma)
        DiscountManager dm2 = DiscountManager.getInstance();

        System.out.println("Descuento global: " + dm2.getDescuentoGlobal() + "%");
        System.out.println("¿DiscountManager es la misma instancia? " + (dm1 == dm2)); // true

        // 3) Probemos con un producto
        Producto polera = new Producto("Polera Oversize", 19990);
        BigDecimal precioOriginal = polera.getPrecio();
        BigDecimal precioConDesc = dm2.aplicarDescuento(precioOriginal);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        System.out.println("Producto: " + polera.getNombre());
        System.out.println("Precio original: " + df.format(precioOriginal.doubleValue()));
        System.out.println("Precio con descuento: " + df.format(precioConDesc.doubleValue()));
    }
}