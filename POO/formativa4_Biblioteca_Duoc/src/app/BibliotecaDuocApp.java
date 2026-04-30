/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import exceptions.BibliotecaException;
import exceptions.DatosInvalidosException;
import exceptions.LibroNoEncontradoException;
import exceptions.LibroYaPrestadoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import model.Libro;
import model.Usuario;

/**
 *
 * @author Felipe Peñaloza & Joaquín Gomez
 */
public class BibliotecaDuocApp {

    public static void main(String[] args) {
        // Colecciones usadas:
        // ArrayList: mantiene orden de ingreso y permite acceso por índice (libros detallados)
        try (Scanner sc = new Scanner(System.in)) {
            // Colecciones usadas:
            // ArrayList: mantiene orden de ingreso y permite acceso por índice (libros detallados)
            List<Libro> listaLibros = new ArrayList<>();
            // HashMap: acceso O(1) a usuarios por clave única (RUT)
            Map<String, Usuario> mapaUsuarios = new HashMap<>();
            // HashSet: garantiza unicidad de libros, sin orden específico
            Set<Libro> conjuntoLibros = new HashSet<>();
            // TreeSet: mantiene orden alfabético (catálogo ordenado)
            Set<Libro> arbolLibros = new TreeSet<>();
            Set<Usuario> arbolUsuarios = new TreeSet<>();
            
            // Datos de ejemplo
            agregarLibro(listaLibros, conjuntoLibros, arbolLibros, new Libro("Java Básico","Ana Torres"));
            agregarLibro(listaLibros, conjuntoLibros, arbolLibros, new Libro("Estructuras de Datos","Luis Fuentes"));
            agregarLibro(listaLibros, conjuntoLibros, arbolLibros, new Libro("Programación Orientada a Objetos","Alberto Campos"));
            
            boolean salir = false;
            while (!salir) {
                imprimirMenu();
                String op = sc.nextLine();
                try {
                    int opcion = Integer.parseInt(op);
                    switch (opcion) {
                        case 1 -> registrarUsuario(sc, mapaUsuarios, arbolUsuarios);
                        case 2 -> buscarUsuario(sc, mapaUsuarios);
                        case 3 -> buscarLibro(sc, listaLibros);
                        case 4 -> prestarLibro(sc, listaLibros);
                        case 5 -> eliminarLibro(sc, listaLibros, conjuntoLibros, arbolLibros);
                        case 6 -> eliminarUsuario(sc, mapaUsuarios, arbolUsuarios);
                        case 7 -> mostrarCatálogo(arbolLibros);
                        case 8 -> salir=true;
                        default -> System.out.println("⚠ Opción inválida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Ingrese un número válido.");
                }
            }
        }
    }

    private static void imprimirMenu() {
        System.out.println("\n*** BIBLIOTECA DUOC UC ***");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Buscar usuario");
        System.out.println("3. Buscar libro");
        System.out.println("4. Prestar libro");
        System.out.println("5. Eliminar libro");
        System.out.println("6. Eliminar usuario");
        System.out.println("7. Ver catálogo ordenado");
        System.out.println("8. Salir");
        System.out.print("Opción: ");
    }

    private static void agregarLibro(List<Libro> list, Set<Libro> set, Set<Libro> tree, Libro libro) {
        if (set.add(libro)) { // asegura unicidad
            list.add(libro);
            tree.add(libro);
            System.out.println("✔ Libro agregado: " + libro.getTitulo());
        } else {
            System.out.println("⚠ El libro ya existe en la colección.");
        }
    }

    private static void registrarUsuario(Scanner sc, Map<String,Usuario> map, Set<Usuario> tree) {
        System.out.print("RUT: "); String rut = sc.nextLine();
        if (map.containsKey(rut)) { System.out.println("⚠ Usuario ya existe."); return; }
        try {
            System.out.print("Sede: "); String sede = sc.nextLine();
            System.out.print("Nombre: "); String nom = sc.nextLine();
            System.out.print("Apellido Paterno: "); String pat = sc.nextLine();
            System.out.print("Apellido Materno: "); String mat = sc.nextLine();
            System.out.print("Teléfono: "); String tel = sc.nextLine();
            Usuario u = new Usuario(sede, rut, nom, pat, mat, tel);
            map.put(rut, u);
            tree.add(u);
            System.out.println("✔ Usuario registrado: " + u.getNombreCompleto());
        } catch (DatosInvalidosException e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    private static void buscarUsuario(Scanner sc, Map<String,Usuario> map) {
        System.out.print("RUT a buscar: "); String rut = sc.nextLine();
        Usuario u = map.get(rut);
        if (u!=null) u.mostrarInformacionUsuario(); else System.out.println("⚠ Usuario no encontrado.");
    }

    private static void buscarLibro(Scanner sc, List<Libro> list) {
        System.out.print("Título: "); String tit = sc.nextLine();
        try {
            Libro l = list.stream()
                .filter(b->b.getTitulo().equalsIgnoreCase(tit))
                .findFirst()
                .orElseThrow(()->new LibroNoEncontradoException("⚠ Libro no encontrado."));
            l.mostrarInformacion();
        } catch (LibroNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void prestarLibro(Scanner sc, List<Libro> list) {
        System.out.print("Título a prestar: "); String tit = sc.nextLine();
        try {
            Libro l = list.stream()
                .filter(b->b.getTitulo().equalsIgnoreCase(tit))
                .findFirst()
                .orElseThrow(()->new LibroNoEncontradoException("⚠ Libro no encontrado."));
            if (!l.estaDisponible()) throw new LibroYaPrestadoException("⚠ Libro ya prestado.");
            l.prestar();
            System.out.println("✔ Préstamo exitoso.");
        } catch (BibliotecaException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void eliminarLibro(Scanner sc, List<Libro> list, Set<Libro> set, Set<Libro> tree) {
        System.out.print("Título a eliminar: "); String tit = sc.nextLine();
        try {
            Libro l = list.stream()
                .filter(b->b.getTitulo().equalsIgnoreCase(tit))
                .findFirst()
                .orElseThrow(()->new LibroNoEncontradoException("⚠ Libro no encontrado."));
            list.remove(l);
            set.remove(l);
            tree.remove(l);
            System.out.println("✔ Libro eliminado: " + tit);
        } catch (LibroNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void eliminarUsuario(Scanner sc, Map<String,Usuario> map, Set<Usuario> tree) {
        System.out.print("RUT a eliminar: "); String rut = sc.nextLine();
        Usuario u = map.remove(rut);
        if (u!=null) {
            tree.remove(u);
            System.out.println("✔ Usuario eliminado: " + rut);
        } else {
            System.out.println("⚠ Usuario no encontrado.");
        }
    }

    private static void mostrarCatálogo(Set<Libro> catalogo) {
        System.out.println("--- Catálogo ordenado alfabéticamente ---");
        catalogo.forEach(Libro::mostrarInformacion);
    }
}
