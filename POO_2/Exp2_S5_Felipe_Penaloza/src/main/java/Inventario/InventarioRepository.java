/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventario;

import Producto.Producto;
import java.util.List;

/**
 * Contrato para repositorios de inventario.
 */
public interface InventarioRepository {
    List<Producto> listarTodos();
    Producto buscarPorId(String id);
    Producto buscarPorNombre(String nombre);
    void agregar(Producto p);
    boolean eliminar(String id);
    boolean existeId(String id);
}