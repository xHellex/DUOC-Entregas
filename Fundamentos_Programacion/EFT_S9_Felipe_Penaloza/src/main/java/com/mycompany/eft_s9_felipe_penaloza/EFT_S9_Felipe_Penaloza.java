/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
   Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java
 */


package com.mycompany.eft_s9_felipe_penaloza;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Sistema de venta de entradas para el Teatro Moro.
 * Permite comprar entradas, ver estado de asientos y generar reportes.
 * Autor: Felipe Peñaloza
 */


public class EFT_S9_Felipe_Penaloza {
    // Constante para el número máximo de entradas/asientos
static final int MAX_ENTRADAS = 100;
    static Entrada[] ventas = new Entrada[MAX_ENTRADAS];
    static Cliente[] clientes = new Cliente[MAX_ENTRADAS];
    static String[] asientos = new String[MAX_ENTRADAS];
    static List<Reserva> reservas = new ArrayList<>();
    static int contadorVentas = 0;
    static Scanner sc = new Scanner(System.in);

    /**
     * Enum que representa las secciones del teatro.
     */
    enum Seccion {
        VIP, PALCO, PLATEA_BAJA, PLATEA_ALTA, GALERIA
    }

    static final Map<Seccion, int[]> seccionRangos = Map.of(
        Seccion.VIP, new int[]{0, 19},
        Seccion.PALCO, new int[]{20, 39},
        Seccion.PLATEA_BAJA, new int[]{40, 59},
        Seccion.PLATEA_ALTA, new int[]{60, 79},
        Seccion.GALERIA, new int[]{80, 99}
    );

    public static void main(String[] args) {
        menu();
    }

    /**
     * Muestra el menú principal del sistema.
     */
    static void menu() {
        int opcion = -1;
        do {
            System.out.println("\n===== MENÚ TEATRO MORO =====");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Ver estado de asientos");
            System.out.println("3. Ver reporte detallado");
            System.out.println("4. Salir");

            System.out.print("Seleccione opción (1-4): ");
            String input = sc.nextLine();

            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número del 1 al 4.");
                continue;
            }

            switch (opcion) {
                case 1 -> comprarEntrada();
                case 2 -> mostrarAsientos();
                case 3 -> reporteDetallado();
                case 4 -> System.out.println("Gracias por usar el sistema.");
                default -> System.out.println("Opción fuera de rango. Intente nuevamente.");
            }
        } while (opcion != 4);
    }

    /**
     * Permite comprar una entrada, registrando cliente, asiento y aplicando descuentos.
     */
    static void comprarEntrada() {
        System.out.print("Ingrese nombre del cliente: ");
        String nombre = sc.nextLine();

        int edad = -1;
        while (edad < 0) {
            System.out.print("Ingrese edad del cliente: ");
            String inputEdad = sc.nextLine();
            try {
                edad = Integer.parseInt(inputEdad);
                if (edad < 0) System.out.println("La edad no puede ser negativa.");
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }

        String genero = "";
        while (!genero.equals("M") && !genero.equals("F")) {
            System.out.print("Ingrese género (M/F): ");
            genero = sc.nextLine().trim().toUpperCase();
            if (!genero.equals("M") && !genero.equals("F")) {
                System.out.println("Debe ingresar 'M' o 'F'.");
            }
        }

        System.out.print("¿Es estudiante? (s/n): ");
        boolean esEstudiante = sc.nextLine().equalsIgnoreCase("s");

        String tipoCliente = determinarTipoCliente(edad, genero, esEstudiante);

        // Selección de sección
        int seleccionSeccion = -1;
        Seccion[] secciones = Seccion.values();
        while (seleccionSeccion < 0 || seleccionSeccion >= secciones.length) {
            System.out.println("Seleccione la sección:");
            for (int i = 0; i < secciones.length; i++) {
                System.out.println((i + 1) + ". " + secciones[i]);
            }
            String input = sc.nextLine();
            try {
                seleccionSeccion = Integer.parseInt(input) - 1;
                if (seleccionSeccion < 0 || seleccionSeccion >= secciones.length) {
                    System.out.println("Selección fuera de rango.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }

        Seccion seccion = secciones[seleccionSeccion];
        int[] rango = seccionRangos.get(seccion);
        if (seccionLlena(rango[0], rango[1])) {
            System.out.println("La sección seleccionada está llena. Por favor elija otra.");
            return;
        }

        mostrarAsientosPorRango(rango[0], rango[1]);

        int asiento = -1;
        while (true) {
            System.out.print("Ingrese número de asiento entre " + rango[0] + " y " + rango[1] + ": ");
            String input = sc.nextLine();
            try {
                asiento = Integer.parseInt(input);
                if (asiento < rango[0] || asiento > rango[1]) {
                    System.out.println("Asiento fuera del rango válido.");
                } else if (asientos[asiento] != null && !asientos[asiento].equals("libre")) {
                    System.out.println("Asiento ya ocupado.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }

        double precioBase = 10000;
        double descuento = obtenerDescuento(tipoCliente);
        double precioFinal = precioBase - (precioBase * descuento);

        asientos[asiento] = "ocupado";
        Cliente cliente = new Cliente(nombre, tipoCliente, edad, genero);
        clientes[contadorVentas] = cliente;
        Entrada entrada = new Entrada(contadorVentas + 1, asiento, precioFinal, seccion, cliente);
        ventas[contadorVentas] = entrada;

        imprimirBoleta(entrada);
        contadorVentas++;
    }

    static boolean seccionLlena(int inicio, int fin) {
        for (int i = inicio; i <= fin; i++) {
            if (asientos[i] == null || asientos[i].equals("libre")) return false;
        }
        return true;
    }

    static void mostrarAsientosPorRango(int inicio, int fin) {
        System.out.println("\nAsientos disponibles en el rango seleccionado:");
        for (int i = inicio; i <= fin; i++) {
            String estado = (asientos[i] == null) ? "libre" : asientos[i];
            System.out.println("Asiento " + i + ": " + estado);
        }
    }

    /**
     * Determina el tipo de cliente en base a sus características.
     */
    static String determinarTipoCliente(int edad, String genero, boolean esEstudiante) {
        Map<String, Double> descuentos = new HashMap<>();
        if (edad <= 12) descuentos.put("niño", 0.10);
        if (edad >= 60) descuentos.put("terceraedad", 0.25);
        if (esEstudiante) descuentos.put("estudiante", 0.15);
        if (genero.equals("F")) descuentos.put("mujer", 0.20);

        return descuentos.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("normal");
    }

    static double obtenerDescuento(String tipo) {
        return switch (tipo) {
            case "niño" -> 0.10;
            case "mujer" -> 0.20;
            case "estudiante" -> 0.15;
            case "terceraedad" -> 0.25;
            default -> 0.0;
        };
    }

    static void imprimirBoleta(Entrada entrada) {
        System.out.println("\n==== BOLETA ====");
        System.out.println("Cliente: " + entrada.cliente.nombre);
        System.out.println("Tipo: " + entrada.cliente.tipo);
        System.out.println("Edad: " + entrada.cliente.edad);
        System.out.println("Género: " + entrada.cliente.genero);
        System.out.println("Sección: " + entrada.seccion);
        System.out.println("Asiento N°: " + entrada.numeroAsiento);
        System.out.printf("Precio: $%.2f\n", entrada.precio);
    }

    /**
     * Muestra el estado actual de los asientos del teatro.
     */
    static void mostrarAsientos() {
        System.out.println("\nEstado de los asientos:");
        for (int i = 0; i < MAX_ENTRADAS; i++) {
            String estado = (asientos[i] == null) ? "libre" : asientos[i];
            System.out.println("Asiento " + i + ": " + estado);
        }
    }

    /**
     * Genera un reporte con datos útiles del negocio: ingresos, ventas por cliente y sección.
     */
    static void reporteDetallado() {
        double ingresoTotal = 0;
        int ocupados = 0, libres = 0;
        Map<String, Integer> ventasPorTipo = new HashMap<>();
        Map<Seccion, Integer> ventasPorSeccion = new EnumMap<>(Seccion.class);

        for (Seccion s : Seccion.values()) {
            ventasPorSeccion.put(s, 0);
        }

        for (int i = 0; i < contadorVentas; i++) {
            Entrada venta = ventas[i];
            if (venta != null) {
                ingresoTotal += venta.precio;
                ventasPorTipo.merge(venta.cliente.tipo, 1, Integer::sum);
                ventasPorSeccion.merge(venta.seccion, 1, Integer::sum);
            }
        }

        for (String estado : asientos) {
            if (estado == null || estado.equals("libre")) libres++;
            else ocupados++;
        }

        System.out.println("\n==== REPORTE DETALLADO ====");
        System.out.printf("Ingreso total: $%.2f\n", ingresoTotal);
        System.out.println("Asientos ocupados: " + ocupados);
        System.out.println("Asientos libres: " + libres);

        System.out.println("\nVentas por tipo de cliente:");
        ventasPorTipo.forEach((tipo, cantidad) ->
            System.out.println("- " + tipo + ": " + cantidad + " entradas"));

        System.out.println("\nVentas por sección:");
        ventasPorSeccion.forEach((seccion, cantidad) ->
            System.out.println("- " + seccion + ": " + cantidad + " entradas"));
    }

    // ==== Clases internas ====

    static class Entrada {
        int idVenta;
        int numeroAsiento;
        double precio;
        Seccion seccion;
        Cliente cliente;

        Entrada(int idVenta, int numeroAsiento, double precio, Seccion seccion, Cliente cliente) {
            this.idVenta = idVenta;
            this.numeroAsiento = numeroAsiento;
            this.precio = precio;
            this.seccion = seccion;
            this.cliente = cliente;
        }
    }

    static class Cliente {
        String nombre;
        String tipo;
        int edad;
        String genero;

        Cliente(String nombre, String tipo, int edad, String genero) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.edad = edad;
            this.genero = genero;
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



/*
PRUEBAS REALIZADAS:

1. Cliente con nombre válido y edad positiva.
2. Ingreso de edad con letras → muestra mensaje de error.
3. Ingreso de género distinto a "M"/"F" → muestra mensaje de error.
4. Ingreso de número de sección inválido (fuera de rango o texto) → muestra error.
5. Selección de asiento ya ocupado → muestra mensaje de advertencia.
6. Cliente estudiante mujer → se aplicó el mayor descuento (20%).
7. Verificación de ingresos totales y reporte por tipo de cliente.
8. Prueba con sección llena → deniega compra.

Todas las pruebas fueron exitosas.
*/