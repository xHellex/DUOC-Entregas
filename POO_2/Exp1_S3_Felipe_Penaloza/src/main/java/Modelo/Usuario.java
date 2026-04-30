package Modelo;

/**
 * Usuario simple para el ejemplo MVC.
 */
public class Usuario {
    private final String id;
    private final String nombre;

    public Usuario(String id, String nombre) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id requerido");
        this.id = id;
        this.nombre = nombre == null || nombre.isBlank() ? "Usuario" : nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
}
