/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import Inventario.Inventario;
import Inventario.InventarioService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuControllerIntegrationTest {

    @Test
    void flujoAgregarListarYSalir() {
        // Entradas en orden: elegir 1 (agregar), id, nombre, descripcion, precio, cantidad, 4(listar), 5(salir)
        TestConsoleIO io = new TestConsoleIO(
            "1",        // opcion: agregar
            "P100",     // ID
            "ProductoX",// Nombre
            "DescX",    // Descripción
            "100.0",    // Precio
            "2",        // Cantidad
            "4",        // Listar
            "5"         // Salir
        );

        Inventario repo = new Inventario();
        InventarioService service = new InventarioService(repo);
        MenuController controller = new MenuController(service, io);

        controller.iniciar(); // ejecuta el flujo completo (usa TestConsoleIO)

        String output = io.getOutput();

        // Validaciones clave
        assertTrue(output.contains("Producto agregado correctamente."), "Debe confirmar agregado");
        assertTrue(output.contains("P100"), "Listado debe contener ID del producto agregado");
        assertTrue(output.contains("Unidades totales"), "Informe debe mostrarse");
    }
}