/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventario;

import Producto.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceIntegrationTest {

    @Test
    void agregarEliminarBuscarGenerarInforme() {
        Inventario repo = new Inventario();
        InventarioService service = new InventarioService(repo);

        Producto p = new Producto("I1", "ProductoA", "desc", 10.0, 1);
        service.agregarProducto(p);

        // El repo debe contener el producto
        assertNotNull(repo.buscarPorId("I1"));
        assertEquals(1, repo.listarTodos().size());

        // Generar informe contiene la ID y el nombre
        String informe = service.generarInforme();
        assertTrue(informe.contains("I1"));
        assertTrue(informe.contains("ProductoA"));

        // Eliminar y comprobar
        assertTrue(service.eliminarProducto("I1"));
        assertNull(repo.buscarPorId("I1"));
        assertEquals(0, repo.listarTodos().size());
    }
}
