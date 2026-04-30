/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventario;

import Producto.Producto;

import java.util.List;

/**
 * Servicio que contiene reglas de negocio del inventario.
 */
public class InventarioService {
    private final InventarioRepository repo;

    public InventarioService(InventarioRepository repo) {
        this.repo = repo;
    }

    public void agregarProducto(Producto p) {
        if (p == null) throw new IllegalArgumentException("Producto nulo.");
        // Reglas de negocio extra: ID único ya verificado en repo
        if (repo.existeId(p.getId())) {
            throw new IllegalArgumentException("Ya existe un producto con el ID: " + p.getId());
        }
        repo.agregar(p);
    }

    public boolean eliminarProducto(String id) {
        return repo.eliminar(id);
    }

    public Producto buscarPorId(String id) {
        return repo.buscarPorId(id);
    }

    public Producto buscarPorNombre(String nombre) {
        return repo.buscarPorNombre(nombre);
    }

    public List<Producto> listarTodos() {
        return repo.listarTodos();
    }

    public String generarInforme() {
        // Si el repo ofrece un informe (como la implementación InMemory), lo usamos:
        if (repo instanceof Inventario) {
            return ((Inventario) repo).generarInforme();
        } else {
            StringBuilder sb = new StringBuilder("=== Informe de Inventario ===\n");
            List<Producto> lista = repo.listarTodos();
            if (lista.isEmpty()) {
                sb.append("No hay productos registrados.\n");
            } else {
                lista.forEach(prod -> sb.append(prod.toString()).append('\n'));
            }
            return sb.toString();
        }
    }
}