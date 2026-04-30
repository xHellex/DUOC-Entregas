/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import DAO.CarteleraDAO;
import Modelo.Pelicula;
import java.util.List;


/**
 *
 * author Joaquín Gómez Flores & Felipe Peñaloza
 */
public class InsertYListar {
    public static void main(String[] args) {
        CarteleraDAO dao = new CarteleraDAO();

        // Insert de prueba
        Pelicula p = new Pelicula("El Origen","Christopher Nolan",2010,148,"CienciaFiccion");
            try {
                Pelicula creada = dao.agregarPelicula(p);
                System.out.println("Insert OK. ID: " + creada.getId());
            } catch (IllegalStateException ise) {
        // duplicado o regla de negocio
            System.out.println("No se insertó: " + ise.getMessage());
            } catch (Exception e) {
        }

        // Listado
        List<Pelicula> lista = List.of(
            new Pelicula("A","D",2020,90,"Drama"),
            new Pelicula("B","D2",2021,100,"Accion")
        );
        try {
            dao.insertarMultipleTransactional(lista);
            System.out.println("Todas insertadas OK");
        } catch (Exception e) {
            System.out.println("Fallo transacción: " + e.getMessage());
        }
    }
}