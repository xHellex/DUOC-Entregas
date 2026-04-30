package App;

/**
 * Menú principal para el sistema de inventario.
 * Autor: Felipe Peñaloza & Joaquín Gómez
 */
import Inventario.Inventario;
import Inventario.InventarioService;

public class MenuPrincipal {
    public static void main(String[] args) {
        ConsoleIO io = new SystemConsoleIO();
        Inventario repo = new Inventario();
        InventarioService service = new InventarioService(repo);
        MenuController controller = new MenuController(service, io);

        // Opcional: precargar datos para demo
        // service.agregarProducto(new Producto("P001","Teclado","Teclado RGB",35000,5));

        controller.iniciar();
    }
}