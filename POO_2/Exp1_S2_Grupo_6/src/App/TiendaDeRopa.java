/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package App;

import Descuento.*;
import Producto.Producto;
import Singleton.Singleton;

import java.text.DecimalFormat;
import java.math.BigDecimal;

/**
 * Demo en main mostrando:
 *  - uso de Singleton
 *  - registro de decoradores para promociones
 *  - ejecución de comandos para aplicar descuentos
 */
public class TiendaDeRopa {
    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // 1) Demo Singleton simple
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println("¿Singleton es la misma instancia? " + (s1 == s2)); // true

        DiscountManager dm = DiscountManager.getInstance();
        dm.setDescuentoGlobal(15.0); // 15% global

        System.out.println("Descuento global: " + dm.getDescuentoGlobal() + "%");

        // 2) Crear producto con categoría
        Producto polera = new Producto("Polera Oversize", 19990, "ROPA");
        BigDecimal original = polera.getPrecio();

        // 3) Registrar decoradores (promociones)
        dm.clearDecorators();
        // primero un 10% general
        dm.addPercentageDecorator(10.0);
        // luego un 20% adicional si categoria == "ROPA"
        dm.addCategoryPercentageDecorator(20.0, "ROPA");
        // además un descuento fijo de 1000
        dm.addFixedAmountDecorator(1000.0);

        // Aplicar decoradores
        BigDecimal precioDecorado = dm.applyDecoratorsToProduct(polera);

        // Aplicar descuento global (por comparación)
        BigDecimal precioGlobal = dm.aplicarDescuento(original);

        System.out.println("Producto: " + polera.getNombre() + " (categoria: " + polera.getCategoria() + ")");
        System.out.println("Precio original: " + df.format(original.doubleValue()));
        System.out.println("Precio aplicando cadena de decoradores: " + df.format(precioDecorado.doubleValue()));
        System.out.println("Precio aplicando solo descuento global: " + df.format(precioGlobal.doubleValue()));

        // 4) Usar CommandInvoker para ejecutar comandos (por ejemplo, aplicar global y luego decoradores)
        CommandInvoker invoker = new CommandInvoker();
        invoker.addCommand(new ApplyGlobalDiscountCommand());
        invoker.addCommand(new ApplyDecoratorCommand());

        BigDecimal lastResult = invoker.executeAll(polera);
        System.out.println("Resultado tras ejecutar comandos (último): " + df.format(lastResult.doubleValue()));
    }
}