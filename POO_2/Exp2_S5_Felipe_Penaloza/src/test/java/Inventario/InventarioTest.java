package Inventario;
import Producto.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Pruebas unitarias para Inventario
 */
import Producto.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {
    private Inventario repo;

    @BeforeEach
    void setUp() {
        repo = new Inventario();
        repo.agregar(new Producto("P001", "Teclado", "Teclado RGB", 35000, 5));
        repo.agregar(new Producto("P002", "Mouse", "Mouse gamer", 15000, 3));
    }

    @Test
    void testAgregarProducto() {
        Producto nuevo = new Producto("P003", "Monitor", "24 pulgadas", 120000, 2);
        repo.agregar(nuevo);
        assertEquals(3, repo.listarTodos().size());
    }

    @Test
    void testAgregarProductoDuplicado() {
        Producto duplicado = new Producto("P001", "Teclado", "Teclado RGB", 35000, 5);
        assertThrows(IllegalArgumentException.class, () -> repo.agregar(duplicado));
    }

    @Test
    void testBuscarPorIdExistente() {
        Producto encontrado = repo.buscarPorId("P001");
        assertNotNull(encontrado);
        assertEquals("Teclado", encontrado.getNombre());
    }

    @Test
    void testBuscarPorIdInexistente() {
        Producto encontrado = repo.buscarPorId("P999");
        assertNull(encontrado);
    }

    @Test
    void testEliminarProducto() {
        assertTrue(repo.eliminar("P001"));
        assertEquals(1, repo.listarTodos().size());
    }

    @Test
    void testListarTodos() {
        List<Producto> productos = repo.listarTodos();
        assertEquals(2, productos.size());
    }
}