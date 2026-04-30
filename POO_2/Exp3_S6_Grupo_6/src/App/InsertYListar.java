/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import DAO.CarteleraDAO;
import Modelo.Pelicula;


/**
 *
 * author Joaquín Gómez Flores & Felipe Peñaloza
 */
public class InsertYListar {
    public static void main(String[] args) {
        CarteleraDAO dao = new CarteleraDAO();

        // Insert de prueba
        Pelicula p = new Pelicula("El Origen", "Christopher Nolan", 2010, 148, "Ciencia Ficción");
        boolean ok = dao.insertar(p);
        System.out.println(ok ? "Insert OK. ID nuevo: " + p.getId() : "No se insertó.");

        // Listado
        System.out.println("Películas en Cartelera:");
        dao.listar().forEach(System.out::println);
    }
    
}
