
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
   Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java
 */

package com.mycompany.exp3_s8_felipe_penaloza;

/**
 *
 * @author Felip
 */
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Exp3_S8_Felipe_Penaloza {
    // Constante para el número máximo de entradas/asientos
    static final int MAX_ENTRADAS = 100;

    // Arreglos y listas para almacenar ventas, clientes, reservas y estado de asientos
    static Entrada[] ventas = new Entrada[MAX_ENTRADAS];
    static Cliente[] clientes = new Cliente[MAX_ENTRADAS];
    static String[] asientos = new String[MAX_ENTRADAS]; // "libre", "ocupado", "reservado"
    static List<Descuento> descuentos = new ArrayList<>();
    static List<Reserva> reservas = new ArrayList<>();

    // Contadores para IDs
    static int contadorVentas = 0;
    static int contadorReservas = 0;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarDescuentos();
        menu();
    }

    // Agrega descuentos por tipo de cliente
    static void inicializarDescuentos() {
        descuentos.add(new Descuento("estudiante", 0.10));
        descuentos.add(new Descuento("terceraedad", 0.15));
    }

    // Menú principal del sistema
    static void menu() {
        int opcion = -1;
        do {
            System.out.println("\n===== MENÚ TEATRO MORO =====");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Reservar asiento");
            System.out.println("3. Confirmar reserva");
            System.out.println("4. Ver estadísticas");
            System.out.println("5. Anular compra");
            System.out.println("6. Anular reserva");
            System.out.println("7. Ver estado de asientos");
            System.out.println("8. Salir");

            System.out.print("Seleccione opción (1-8): ");
            String input = sc.nextLine();

            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número del 1 al 8.");
                continue;
            }

            switch (opcion) {
                case 1 -> comprarEntrada();
                case 2 -> reservarAsiento();
                case 3 -> confirmarReserva();
                case 4 -> verEstadisticas();
                case 5 -> anularCompra();
                case 6 -> anularReserva();
                case 7 -> mostrarAsientos();
                case 8 -> System.out.println("Gracias por usar el sistema.");
                default -> System.out.println("Opción fuera de rango. Intente nuevamente.");
            }
        } while (opcion != 8);
    }

    // Proceso de compra de entrada
    static void comprarEntrada() {
        String tipo;
        while (true) {
            System.out.print("Tipo de cliente (normal/estudiante/terceraedad): ");
            tipo = sc.nextLine().toLowerCase();
            if (tipo.equals("normal") || tipo.equals("estudiante") || tipo.equals("terceraedad")) break;
            System.out.println("Tipo de cliente inválido.");
        }

        int asiento = buscarAsientoLibre();
        if (asiento == -1) {
            System.out.println("No hay asientos disponibles.");
            return;
        }

        double precioBase = 10000;
        double descuento = obtenerDescuento(tipo);
        double precioFinal = precioBase - (precioBase * descuento);

        asientos[asiento] = "ocupado";
        ventas[contadorVentas] = new Entrada(contadorVentas + 1, asiento, precioFinal);

        System.out.print("Ingrese nombre del cliente: ");
        String nombre = sc.nextLine();
        clientes[contadorVentas] = new Cliente(nombre, tipo);

        contadorVentas++;

        System.out.println("Entrada comprada para asiento " + asiento + " a $" + precioFinal);
    }

    // Reserva un asiento libre
    static void reservarAsiento() {
        int asiento = buscarAsientoLibre();
        if (asiento == -1) {
            System.out.println("No hay asientos disponibles.");
            return;
        }

        asientos[asiento] = "reservado";
        Reserva reserva = new Reserva(contadorReservas + 1, contadorReservas + 1, asiento);
        reservas.add(reserva);
        contadorReservas++;

        System.out.println("Asiento " + asiento + " reservado. ID de reserva: " + reserva.idReserva);
    }

    // Confirma una reserva existente
    static void confirmarReserva() {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }

        System.out.print("Ingrese ID de reserva: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Reserva r = reservas.stream().filter(res -> res.idReserva == id).findFirst().orElse(null);

        if (r == null) {
            System.out.println("Reserva no encontrada.");
            return;
        }

        asientos[r.numeroAsiento] = "ocupado";
        reservas.remove(r);
        System.out.println("Reserva confirmada para asiento " + r.numeroAsiento);
    }

    // Muestra estadísticas básicas
    static void verEstadisticas() {
        System.out.println("\nEntradas vendidas: " + contadorVentas);
        System.out.println("Reservas activas: " + reservas.size());
        int disponibles = (int) Arrays.stream(asientos).filter(a -> a == null || a.equals("libre")).count();
        System.out.println("Asientos disponibles: " + disponibles);
    }

    // Anula una compra realizada
    static void anularCompra() {
        System.out.print("Ingrese ID de venta: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        for (int i = 0; i < contadorVentas; i++) {
            if (ventas[i] != null && ventas[i].idVenta == id) {
                asientos[ventas[i].numeroAsiento] = "libre";
                ventas[i] = null;
                clientes[i] = null;
                System.out.println("Compra anulada.");
                return;
            }
        }

        System.out.println("Venta no encontrada.");
    }

    // Anula una reserva registrada
    static void anularReserva() {
        System.out.print("Ingrese ID de reserva: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Reserva r = reservas.stream().filter(res -> res.idReserva == id).findFirst().orElse(null);
        if (r == null) {
            System.out.println("Reserva no encontrada.");
            return;
        }

        asientos[r.numeroAsiento] = "libre";
        reservas.remove(r);
        System.out.println("Reserva anulada.");
    }

    // Muestra el estado actual de los asientos
    static void mostrarAsientos() {
        System.out.println("\nEstado de los asientos:");
        for (int i = 0; i < MAX_ENTRADAS; i++) {
            String estado = (asientos[i] == null) ? "libre" : asientos[i];
            System.out.println("Asiento " + i + ": " + estado);
        }
    }

    // Busca el primer asiento disponible
    static int buscarAsientoLibre() {
        for (int i = 0; i < MAX_ENTRADAS; i++) {
            if (asientos[i] == null || asientos[i].equals("libre")) return i;
        }
        return -1;
    }

    // Obtiene el porcentaje de descuento según el tipo de cliente
    static double obtenerDescuento(String tipo) {
        return descuentos.stream()
                .filter(d -> d.tipoCliente.equals(tipo))
                .map(d -> d.porcentaje)
                .findFirst()
                .orElse(0.0);
    }

    // ---------------- CLASES AUXILIARES ----------------

    static class Entrada {
        int idVenta;
        int numeroAsiento;
        double precio;

        Entrada(int idVenta, int numeroAsiento, double precio) {
            this.idVenta = idVenta;
            this.numeroAsiento = numeroAsiento;
            this.precio = precio;
        }
    }

    static class Cliente {
        String nombre;
        String tipo;

        Cliente(String nombre, String tipo) {
            this.nombre = nombre;
            this.tipo = tipo;
        }
    }

    static class Descuento {
        String tipoCliente;
        double porcentaje;

        Descuento(String tipoCliente, double porcentaje) {
            this.tipoCliente = tipoCliente;
            this.porcentaje = porcentaje;
        }
    }

    static class Reserva {
        int idReserva;
        int idCliente;
        int numeroAsiento;

        Reserva(int idReserva, int idCliente, int numeroAsiento) {
            this.idReserva = idReserva;
            this.idCliente = idCliente;
            this.numeroAsiento = numeroAsiento;
        }
    }
}