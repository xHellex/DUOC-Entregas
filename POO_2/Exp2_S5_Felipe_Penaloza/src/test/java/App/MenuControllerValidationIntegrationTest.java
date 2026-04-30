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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuControllerValidationIntegrationTest {

    @Test
    void agregarConPrecioInvalidoLuegoValido() {
        // Secuencia: elegir agregar, id, nombre, desc, precio inválido ("abc"), precio válido ("120.5"), cantidad, listar, salir
        TestConsoleIO io = new TestConsoleIO(
            "1",
            "P201",
            "ProdVal",
            "DescVal",
            "abc",      // precio inválido -> deberia provocar reintento
            "120.5",    // precio válido
            "3",        // cantidad
            "4",
            "5"
        );

        Inventario repo = new Inventario();
        InventarioService service = new InventarioService(repo);
        MenuController controller = new MenuController(service, io);

        controller.iniciar();

        String out = io.getOutput();
        // El controller muestra mensaje de error al parsear double y luego proceso correcto
        assertTrue(out.contains("Debe ingresar un número válido"), "Debe avisar precio inválido");
        assertTrue(out.contains("Producto agregado correctamente."), "Producto debe agregarse luego del reintento");
        assertTrue(out.contains("P201"), "Listado debe mostrar producto con ID P201");
    }
}