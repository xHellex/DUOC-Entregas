package App;

import Controlador.*;
import Vista.*;
import Producto.Producto;
import Modelo.*;
import Descuento.DiscountManager;
import Comand.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * TiendaDeRopa: interfaz de consola ligera con validaciones.
 */
public class TiendaDeRopa {

    private static final Scanner SC = new Scanner(System.in);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    public static void main(String[] args) {
        // Inicialización MVC
        ProductController productController = new ProductController();
        ConsoleProductView productView = new ConsoleProductView();
        ConsoleCartView cartView = new ConsoleCartView();
        ConsoleDiscountView discountView = new ConsoleDiscountView();

        // Poblamos catálogo de ejemplo
        productController.agregarProducto(new Producto("SH-001", "Polera Oversize", 19990.0, "ROPA"));
        productController.agregarProducto(new Producto("JN-010", "Jeans regular", 35000.0, "ROPA"));
        productController.agregarProducto(new Producto("AC-001", "Gorra", 7990.0, "ACCESORIOS"));

        // Crear usuario y pedido
        Usuario usuario = new Usuario("u-1", "Felipe");
        Pedido pedido = new Pedido("pedido-1", usuario.getId());
        CartController cartController = new CartController(pedido);
        DiscountManager dm = DiscountManager.getInstance();

        // Loop principal
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== TiendaDeRopa - Menú ===");
            System.out.println("1) Mostrar catálogo");
            System.out.println("2) Agregar producto al pedido");
            System.out.println("3) Mostrar pedido / subtotal");
            System.out.println("4) Configurar descuentos (global / decorators)");
            System.out.println("5) Mostrar totales con descuentos");
            System.out.println("6) Ejecutar secuencia de comandos (global -> decoradores)");
            System.out.println("0) Salir");
            System.out.print("Elige opción: ");

            String opt = SC.nextLine().trim();
            switch (opt) {
                case "1":
                    productView.mostrarCatalogo(productController.listarProductos());
                    break;
                case "2":
                    manejarAgregarProducto(productController, cartController);
                    break;
                case "3":
                    cartView.mostrarPedido(pedido);
                    break;
                case "4":
                    manejarConfigurarDescuentos(dm);
                    break;
                case "5":
                    mostrarTotales(cartController, dm, discountView);
                    break;
                case "6":
                    ejecutarComandosDemo(dm, pedido.getItems().isEmpty() ? null : pedido.getItems().get(0).getProducto());
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
        System.out.println("Saliendo. ¡Hasta luego!");
    }

    private static void manejarAgregarProducto(ProductController pc, CartController cc) {
        System.out.print("Ingrese SKU del producto a agregar: ");
        String sku = SC.nextLine().trim();
        if (sku.isEmpty()) {
            System.out.println("SKU no puede estar vacío.");
            return;
        }
        Producto p = pc.buscarPorSku(sku);
        if (p == null) {
            System.out.println("Producto no encontrado para SKU: " + sku);
            return;
        }
        System.out.print("Ingrese cantidad (entero > 0): ");
        String cantidadStr = SC.nextLine().trim();
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                System.out.println("Cantidad inválida, debe ser > 0.");
                return;
            }
            cc.agregarProducto(p, cantidad);
            System.out.println("Producto agregado: " + p.getNombre() + " x" + cantidad);
        } catch (NumberFormatException ex) {
            System.out.println("Cantidad inválida. Debes ingresar un número entero.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void manejarConfigurarDescuentos(DiscountManager dm) {
        System.out.println("--- Configurar descuentos ---");
        System.out.println("a) Setear descuento global (porcentaje)");
        System.out.println("b) Agregar PercentageDecorator (porcentaje)");
        System.out.println("c) Agregar CategoryPercentageDecorator (porcentaje, categoria)");
        System.out.println("d) Agregar FixedAmountDecorator (monto fijo)");
        System.out.println("e) Limpiar decoradores");
        System.out.print("Elige opción: ");
        String opt = SC.nextLine().trim().toLowerCase();
        try {
            switch (opt) {
                case "a":
                    System.out.print("Ingrese porcentaje global (0-100): ");
                    double pg = Double.parseDouble(SC.nextLine().trim());
                    dm.setDescuentoGlobal(pg);
                    System.out.println("Descuento global seteado a " + dm.getDescuentoGlobal() + "%");
                    break;
                case "b":
                    System.out.print("Ingrese porcentaje para PercentageDecorator (0-100): ");
                    double p = Double.parseDouble(SC.nextLine().trim());
                    dm.addPercentageDecorator(p);
                    System.out.println("PercentageDecorator agregado (" + p + "%).");
                    break;
                case "c":
                    System.out.print("Ingrese porcentaje para CategoryPercentageDecorator (0-100): ");
                    double pcPct = Double.parseDouble(SC.nextLine().trim());
                    System.out.print("Ingrese categoría (ej: ROPA): ");
                    String cat = SC.nextLine().trim();
                    dm.addCategoryPercentageDecorator(pcPct, cat);
                    System.out.println("CategoryPercentageDecorator agregado (" + pcPct + "% para " + cat + ").");
                    break;
                case "d":
                    System.out.print("Ingrese monto fijo a descontar (ej: 1000): ");
                    double amt = Double.parseDouble(SC.nextLine().trim());
                    dm.addFixedAmountDecorator(amt);
                    System.out.println("FixedAmountDecorator agregado (" + amt + ").");
                    break;
                case "e":
                    dm.clearDecorators();
                    System.out.println("Decoradores limpiados.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Entrada numérica inválida.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Error de validación: " + iae.getMessage());
        } catch (Exception ex) {
            System.out.println("Error inesperado: " + ex.getMessage());
        }
    }

    private static void mostrarTotales(CartController cc, DiscountManager dm, ConsoleDiscountView dv) {
        BigDecimal subtotal = cc.obtenerSubtotal();
        System.out.println("Subtotal actual: " + DF.format(subtotal.doubleValue()));
        BigDecimal global = dm.aplicarDescuento(subtotal);
        System.out.println("Total aplicando descuento global (" + dm.getDescuentoGlobal() + "%): " + DF.format(global.doubleValue()));
        BigDecimal decorado = cc.aplicarDecoradoresYSumar();
        System.out.println("Total aplicando decoradores (producto por producto): " + DF.format(decorado.doubleValue()));
    }

    private static void ejecutarComandosDemo(DiscountManager dm, Producto productoParaDemo) {
        if (productoParaDemo == null) {
            System.out.println("Agrega al menos un producto al pedido antes de ejecutar comandos (opción 2).");
            return;
        }
        try {
            ComandInvoker inv = new ComandInvoker();
            inv.addComand(new ApplyGlobalDiscountComand());
            inv.addComand(new ApplyDecoratorComand());
            BigDecimal result = inv.executeAll(productoParaDemo);
            System.out.println("Resultado secuencia comandos (último): " + DF.format(result.doubleValue()));
        } catch (Exception ex) {
            System.out.println("Error al ejecutar comandos: " + ex.getMessage());
        }
    }
}
