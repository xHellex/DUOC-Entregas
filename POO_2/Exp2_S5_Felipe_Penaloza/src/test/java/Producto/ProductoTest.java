package Producto;
import Producto.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {
    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("P001", "Teclado", "Teclado mecánico RGB", 35000, 5);
    }

    @Test
    void testCrearProducto() {
        assertNotNull(producto);
        assertEquals("P001", producto.getId());
        assertEquals("Teclado", producto.getNombre());
        assertEquals("Teclado mecánico RGB", producto.getDescripcion());
        assertEquals(35000, producto.getPrecio());
        assertEquals(5, producto.getCantidad());
    }

    @Test
    void testActualizarStock() {
        producto.actualizarStock(10);
        assertEquals(10, producto.getCantidad());
    }

    @Test
    void testActualizarPrecio() {
        producto.actualizarPrecio(30000);
        assertEquals(30000, producto.getPrecio());
    }

    @Test
    void testCalcularTotal() {
        assertEquals(175000, producto.calcularTotal());
    }

    @Test
    void testSetPrecioInvalido() {
        assertThrows(IllegalArgumentException.class, () -> producto.setPrecio(-1000));
    }

    @Test
    void testSetCantidadInvalida() {
        assertThrows(IllegalArgumentException.class, () -> producto.setCantidad(-5));
    }

    @Test
    void testIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("ID INVALIDO!", "X", "", 1000, 1));
    }
}