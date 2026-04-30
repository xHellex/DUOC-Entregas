/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

/**
 *
 * @author Felip
 */
import Inventario.Inventario;
import Inventario.InventarioService;
import Inventario.InventarioRepository;
import Producto.Producto;

import java.util.List;

/**
 * Controller que maneja la lógica del menú usando ConsoleIO e InventarioService.
 */
public class MenuController {
    private final InventarioService service;
    private final ConsoleIO io;

    public MenuController(InventarioService service, ConsoleIO io) {
        this.service = service;
        this.io = io;
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            procesarOpcion(opcion);
            io.println("");
        } while (opcion != 5);
    }

    private void mostrarMenu() {
        io.println("===== MENÚ INVENTARIO =====");
        io.println("1) Agregar producto");
        io.println("2) Eliminar producto por ID");
        io.println("3) Buscar producto (por ID o por nombre)");
        io.println("4) Listar todos los productos");
        io.println("5) Salir");
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> agregarProducto();
            case 2 -> eliminarProducto();
            case 3 -> buscarProducto();
            case 4 -> listarProductos();
            case 5 -> io.println("Saliendo del sistema...");
            default -> io.println("Opción inválida. Intente nuevamente.");
        }
    }

    private void agregarProducto() {
        io.println("=== Agregar producto ===");
        String id = readString("ID: ");
        String nombre = readString("Nombre: ");
        String descripcion = readString("Descripción: ");
        double precio = leerDouble("Precio: ");
        int cantidad = leerEntero("Cantidad: ");

        try {
            Producto p = new Producto(id, nombre, descripcion, precio, cantidad);
            service.agregarProducto(p);
            io.println("Producto agregado correctamente.");
        } catch (IllegalArgumentException ex) {
            io.println("No se pudo agregar el producto: " + ex.getMessage());
        }
    }

    private void eliminarProducto() {
        List<Producto> lista = service.listarTodos();
        if (lista.isEmpty()) {
            io.println("Inventario vacío.");
            return;
        }
        io.println("=== Eliminar producto ===");
        String id = readString("Ingrese ID a eliminar: ");
        boolean eliminado = service.eliminarProducto(id);
        io.println(eliminado ? "Producto eliminado." : "No se encontró un producto con ese ID.");
    }

    private void buscarProducto() {
        List<Producto> lista = service.listarTodos();
        if (lista.isEmpty()) {
            io.println("Inventario vacío.");
            return;
        }
        io.println("=== Buscar producto ===");
        io.println("1) Buscar por ID");
        io.println("2) Buscar por nombre");
        int tipo = leerEntero("Seleccione: ");

        Producto encontrado = null;
        switch (tipo) {
            case 1 -> {
                String id = readString("ID: ");
                encontrado = service.buscarPorId(id);
            }
            case 2 -> {
                String nombre = readString("Nombre: ");
                encontrado = service.buscarPorNombre(nombre);
            }
            default -> {
                io.println("Opción de búsqueda inválida.");
                return;
            }
        }

        if (encontrado == null) {
            io.println("Sin resultados.");
        } else {
            io.println(encontrado.toString());
        }
    }

    private void listarProductos() {
        io.println("=== Listado de productos ===");
        List<Producto> lista = service.listarTodos();
        if (lista.isEmpty()) {
            io.println("Inventario vacío.");
        } else {
            lista.forEach(p -> io.println(p.toString()));
        }
        io.println(service.generarInforme());
    }

    // --- Utilidades de lectura seguras ---
    private int leerEntero(String mensaje) {
        while (true) {
            String linea = readString(mensaje);
            try {
                return Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                io.println("Debe ingresar un número entero válido.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            String linea = readString(mensaje);
            try {
                return Double.parseDouble(linea.trim());
            } catch (NumberFormatException e) {
                io.println("Debe ingresar un número válido (use punto como separador decimal).");
            }
        }
    }

    private String readString(String prompt) {
        return io.readLine(prompt).trim();
    }
}