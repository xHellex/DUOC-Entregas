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
import java.util.List;
import java.util.Scanner;
import model.Libro;
import model.Usuario;

/**
 *
 * @author Felipe Peñaloza & Joaquín Gomez
 */
public class BibliotecaDuocApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Usuario> usuarios = new ArrayList<>();
        List<Libro> libros = new ArrayList<>();

        // carga de CSV comentada: no se usará integración con archivos por el momento
        // try {
        //     BibliotecaService.cargarLibrosDesdeCSV("data/libros.csv");
        //     BibliotecaService.cargarUsuariosDesdeCSV("data/usuarios.csv");
        // } catch (IOException | DatosInvalidosException e) {
        //     System.err.println("Error al cargar datos: " + e.getMessage());
        // }

        // Datos estáticos de ejemplo
        libros.add(new Libro("Java Básico", "Ana Torres"));
        libros.add(new Libro("Estructuras de Datos", "Luis Fuentes"));
        libros.add(new Libro("Programación Orientada a Objetos", "Alberto Campos"));
        libros.add(new Libro("Habilidades de Comunicación", "Diego Díaz"));

        boolean salir = false;
        while (!salir) {
            imprimirMenu();
            System.out.print("Seleccione una opción: ");
            String entrada = sc.nextLine();

            try {
                int opcion = Integer.parseInt(entrada);
                switch (opcion) {
                    case 1:
                        registrarUsuario(sc, usuarios);
                        break;
                    case 2:
                        buscarUsuario(sc, usuarios);
                        break;
                    case 3:
                        buscarLibro(sc, libros);
                        break;
                    case 4:
                        prestarLibro(sc, libros);
                        break;
                    case 5:
                        System.out.println("Gracias por usar Bibliotecas DUOC UC. ¡Hasta luego!");
                        salir = true;
                        break;
                    default:
                        System.out.println("⚠ Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Debes ingresar un número." + e.getMessage());
            }
        }
        sc.close();
    }

    private static void imprimirMenu() {
        System.out.println("\n*** BIBLIOTECAS DUOC UC ***");
        System.out.println("1. Registrar nuevo usuario");
        System.out.println("2. Buscar un usuario");
        System.out.println("3. Buscar un libro");
        System.out.println("4. Prestar un libro");
        System.out.println("5. Salir");
    }

    private static void registrarUsuario(Scanner sc, List<Usuario> usuarios) {
        System.out.print("RUT (12.345.678-9): ");
        String rut = sc.nextLine();
        if (usuarios.stream().anyMatch(u -> u.getRut().equals(rut))) {
            System.out.println("⚠ Ya existe un usuario con ese RUT.");
            return;
        }
        if (!rut.matches("^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$")) {
            System.out.println("⚠ Formato de RUT inválido.");
            return;
        }

        try {
            System.out.print("Sede: "); String sede = sc.nextLine();
            System.out.print("Nombre: "); String nombre = sc.nextLine();
            System.out.print("Apellido Paterno: "); String apPat = sc.nextLine();
            System.out.print("Apellido Materno: "); String apMat = sc.nextLine();
            System.out.print("Teléfono: "); String telefono = sc.nextLine();

            Usuario nuevo = new Usuario(sede, rut, nombre, apPat, apMat, telefono);
            usuarios.add(nuevo);
            System.out.println("✔ Usuario registrado correctamente.");
        } catch (DatosInvalidosException e) {
            System.out.println("⚠ Error al registrar usuario: " + e.getMessage());
        }
    }

    private static void buscarUsuario(Scanner sc, List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("⚠ No hay usuarios registrados.");
            return;
        }
        System.out.print("RUT a buscar: ");
        String rut = sc.nextLine();
        usuarios.stream()
            .filter(u -> u.getRut().equals(rut))
            .findFirst()
            .ifPresentOrElse(
                Usuario::mostrarInformacionUsuario,
                () -> System.out.println("⚠ Usuario no encontrado.")
            );
    }

    private static void buscarLibro(Scanner sc, List<Libro> libros) {
        System.out.print("Título a buscar: ");
        String titulo = sc.nextLine();
        try {
            Libro libro = libros.stream()
                .filter(l -> l.getTitulo().equalsIgnoreCase(titulo))
                .findFirst()
                .orElseThrow(() -> new LibroNoEncontradoException("⚠ Libro no encontrado."));
            libro.mostrarInformacion();
        } catch (LibroNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void prestarLibro(Scanner sc, List<Libro> libros) {
        System.out.print("Título a prestar: ");
        String titulo = sc.nextLine();
        try {
            Libro libro = libros.stream()
                .filter(l -> l.getTitulo().equalsIgnoreCase(titulo))
                .findFirst()
                .orElseThrow(() -> new LibroNoEncontradoException("⚠ Libro no encontrado."));
            if (!libro.estaDisponible()) {
                throw new LibroYaPrestadoException("⚠ El libro ya está prestado.");
            }
            libro.prestar();
            System.out.println("✔ Préstamo exitoso.");
        } catch (BibliotecaException e) {
            System.out.println(e.getMessage());
        }
    }
}
