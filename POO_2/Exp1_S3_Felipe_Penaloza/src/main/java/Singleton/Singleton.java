package Singleton;

/**
 * Clase de ejemplo que demuestra el patrón singleton con instancia privada, estática y final.
 */
public class Singleton {
    private static final Singleton INSTANCIA = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCIA;
    }
}
