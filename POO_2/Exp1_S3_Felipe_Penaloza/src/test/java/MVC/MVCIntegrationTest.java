package MVC;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Controlador.*;
import Producto.Producto;
import Modelo.Pedido;
import Modelo.Usuario;
import Descuento.DiscountManager;

import java.math.BigDecimal;

public class MVCIntegrationTest {

    private ProductController pc;
    private CartController cc;
    private DiscountManager dm;

    @BeforeEach
    public void setUp() {
        pc = new ProductController();
        Producto p1 = new Producto("SH-001","Polera",100.00,"ROPA");
        Producto p2 = new Producto("AC-01","Gorra",50.00,"ACCESORIOS");
        pc.agregarProducto(p1);
        pc.agregarProducto(p2);

        Usuario u = new Usuario("u1","Test");
        Pedido ped = new Pedido("ped1", u.getId());
        cc = new CartController(ped);
        dm = DiscountManager.getInstance();
        dm.setDescuentoGlobal(0.0);
        dm.clearDecorators();
    }

    @Test
    public void testAddRemoveAndSubtotal() {
        Producto p = pc.buscarPorSku("SH-001");
        cc.agregarProducto(p,2);
        assertEquals(200.00, cc.obtenerSubtotal().doubleValue(), 0.001);
        cc.eliminarProducto("SH-001");
        assertEquals(0.00, cc.obtenerSubtotal().doubleValue(), 0.001);
    }

    @Test
    public void testApplyDecoratorsInCart() {
        Producto p = pc.buscarPorSku("SH-001");
        cc.agregarProducto(p,1);
        dm.addPercentageDecorator(10.0); // 10% -> 90
        dm.addCategoryPercentageDecorator(20.0,"ROPA"); // -> 72
        BigDecimal total = cc.aplicarDecoradoresYSumar();
        assertEquals(72.00, total.doubleValue(), 0.001);
    }
}
