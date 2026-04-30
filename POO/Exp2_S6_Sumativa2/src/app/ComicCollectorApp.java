/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import exceptions.BusinessException;
import java.util.Scanner;
import model.Usuario;
import service.ComicCollectorService;

/**
 *
 * @author Felip
 */
public class ComicCollectorApp {
    public static void main(String[] args) {
        ComicCollectorService svc = new ComicCollectorService();
        svc.cargarComicsCsv("data/comics.csv");
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("""
                \n=== COMIC COLLECTOR SYSTEM ===
                1. Listar cómics
                2. Ver catálogo ordenado
                3. Registrar usuario
                4. Listar usuarios
                5. Reservar cómic
                6. Salir
                Opción:""");
            String op = sc.nextLine();
            try {
                switch (Integer.parseInt(op)) {
                    case 1 -> svc.listarComics();
                    case 2 -> svc.mostrarCatalogoOrdenado();
                    case 3 -> {
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Nombre: ");
                        String nombre = sc.nextLine();
                        svc.registrarUsuario(new Usuario(email, nombre));
                        System.out.println("✔ Usuario registrado.");
                    }
                    case 4 -> svc.listarUsuarios();
                    case 5 -> {
                        System.out.print("Título: ");
                        String t = sc.nextLine();
                        System.out.print("Número: ");
                        int n = Integer.parseInt(sc.nextLine());
                        svc.reservarComic(t, n);
                        System.out.println("✔ Cómic reservado.");
                    }
                    case 6 -> salir = true;
                    default -> System.out.println("⚠ Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Debe ingresar un número.");
            } catch (BusinessException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }
        sc.close();
    }
}