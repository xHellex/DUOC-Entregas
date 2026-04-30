/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventario;

import Producto.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceTest {
    private Inventario repo;
    private InventarioService service;

    @BeforeEach
    void setUp() {
        repo = new Inventario();
        service = new InventarioService(repo);
    }

    @Test
    void agregarYBuscar() {
        Producto p = new Producto("S1", "ProductoX", "desc", 100.0, 2);
        service.agregarProducto(p);
        assertNotNull(service.buscarPorId("S1"));
    }

    @Test
    void agregarDuplicadoLanza() {
        Producto p1 = new Producto("S2", "A", "", 10, 1);
        service.agregarProducto(p1);
        Producto p2 = new Producto("S2", "B", "", 20, 2);
        assertThrows(IllegalArgumentException.class, () -> service.agregarProducto(p2));
    }
}