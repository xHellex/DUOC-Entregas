
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
   Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java
 */

package com.mycompany.exp2_s6_felipe_penaloza;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Exp2_S6_Felipe_Penaloza {
    static int totalEntradasVendidas = 0;
    static double totalIngresos = 0;
    static int totalReservas = 0;
    static int entradasDisponibles = 100;
    static final String nombreTeatro = "Teatro Moro";

    static class Entrada {
        int numero;
        String ubicacion;
        double precioFinal;
        String tipoCliente;
        boolean esReserva;

        Entrada(int numero, String ubicacion, double precioFinal, String tipoCliente, boolean esReserva) {
            this.numero = numero;
            this.ubicacion = ubicacion;
            this.precioFinal = precioFinal;
            this.tipoCliente = tipoCliente;
            this.esReserva = esReserva;
        }

        @Override
        public String toString() {
            return "N°: " + numero + ", Ubicación: " + ubicacion + ", Precio: $" + precioFinal + ", Tipo: " + tipoCliente + (esReserva ? " (RESERVA)" : "");
        }
    }

    static List<Entrada> entradas = new ArrayList<>();
    static int contadorEntradas = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> reservarEntrada(sc);
                case 2 -> comprarEntradas(sc);
                case 3 -> modificarVenta(sc);
                case 4 -> imprimirBoletas(sc);
                case 5 -> mostrarEstadisticas();
                case 6 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    static void mostrarMenu() {
        System.out.println("\n===== MENÚ TEATRO MORO =====");
        System.out.println("Bienvenido al Teatro Moro");
        System.out.println("1. Reservar entradas");
        System.out.println("2. Comprar entradas");
        System.out.println("3. Modificar venta existente");
        System.out.println("4. Imprimir boleta");
        System.out.println("5. Ver estadísticas");
        System.out.println("6. Salir");
    }

    static void reservarEntrada(Scanner sc) {
        procesarEntrada(sc, true, 1);
        totalReservas++;
    }

    static void comprarEntradas(Scanner sc) {
        if (entradasDisponibles <= 0) {
            System.out.println("No hay entradas disponibles para comprar.");
            return;
        }
        System.out.print("¿Cuántas entradas desea comprar?: ");
        int cantidad = sc.nextInt();
        sc.nextLine();
        if (cantidad > entradasDisponibles) {
            System.out.println("No hay suficientes entradas disponibles. Máximo: " + entradasDisponibles);
            return;
        }
        for (int i = 0; i < cantidad; i++) {
            procesarEntrada(sc, false, cantidad);
            entradasDisponibles--;
        }
    }

    static void modificarVenta(Scanner sc) {
        System.out.print("Ingrese número de entrada a modificar: ");
        int num = sc.nextInt();
        sc.nextLine();

        Entrada entradaModificada = entradas.stream().filter(e -> e.numero == num).findFirst().orElse(null);

        if (entradaModificada == null) {
            System.out.println("No se encontró la entrada con ese número.");
            return;
        }

        if (entradaModificada.esReserva) {
            System.out.println("Entrada encontrada como reserva");
            System.out.print("¿Desea confirmar esta reserva como compra? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                entradaModificada.esReserva = false;
                totalReservas--;
                totalEntradasVendidas++;
                totalIngresos += entradaModificada.precioFinal;
                entradasDisponibles--; // ahora se descuenta del total
                System.out.println("Reserva confirmada como compra: " + entradaModificada);
            }
        } else {
            System.out.println("Entrada encontrada como comprada.");
            System.out.print("¿Desea eliminar esta entrada? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                entradas.remove(entradaModificada);
                totalEntradasVendidas--;
                totalIngresos -= entradaModificada.precioFinal;
                entradasDisponibles++;
                System.out.println("Entrada eliminada exitosamente.");
            }
        }
    }

    static void imprimirBoletas(Scanner sc) {
        System.out.print("Ingrese números de entrada a imprimir (separados por coma): ");
        String[] nums = sc.nextLine().split(",");
        double totalBoleta = 0;
        System.out.println("--- BOLETA TEATRO MORO ---");
        System.out.println("Teatro: " + nombreTeatro);
        for (String numStr : nums) {
            try {
                int num = Integer.parseInt(numStr.trim());
                Entrada e = entradas.stream().filter(en -> en.numero == num).findFirst().orElse(null);
                if (e != null && !e.esReserva) {
                    System.out.println(e);
                    totalBoleta += e.precioFinal;
                } else if (e != null) {
                    System.out.println("La entrada N° " + e.numero + " está reservada. Debe confirmarla antes.");
                } else {
                    System.out.println("Entrada N° " + num + " no encontrada.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Número inválido: " + numStr);
            }
        }
        System.out.println("Costo total: $" + totalBoleta);
        System.out.println("--- Disfrute su función ---");
        System.out.println("-------------------");
    }

    static void mostrarEstadisticas() {
        System.out.println("--- ESTADÍSTICAS ---");
        System.out.println("Entradas vendidas: " + totalEntradasVendidas);
        System.out.println("Entradas reservadas: " + totalReservas);
        System.out.println("Ingresos totales: $" + totalIngresos);
        System.out.println("Entradas disponibles: " + entradasDisponibles);
    }

    static void procesarEntrada(Scanner sc, boolean reserva, int totalEntradasProceso) {
        String[] ubicacionesValidas = {"vip", "platea", "general"};
        double precio = 0;
        double precioVIP = 20000, precioPlatea = 15000, precioGeneral = 10000;

        System.out.print("Ubicación (VIP/Platea/General): ");
        String ubicacion = sc.nextLine().toLowerCase();
        while (!Arrays.asList(ubicacionesValidas).contains(ubicacion)) {
            System.out.print("Ubicación inválida. Intente nuevamente: ");
            ubicacion = sc.nextLine().toLowerCase();
        }

        switch (ubicacion) {
            case "vip" -> precio = precioVIP;
            case "platea" -> precio = precioPlatea;
            case "general" -> precio = precioGeneral;
        }

        System.out.print("Tipo cliente (Normal/Estudiante/TerceraEdad): ");
        String tipo = sc.nextLine().toLowerCase();
        double descuento = switch (tipo) {
            case "estudiante" -> 0.10;
            case "terceraedad" -> 0.15;
            default -> 0.0;
        };

        double precioFinal = precio - (precio * descuento);
        if (!reserva && totalEntradasProceso >= 3) {
            precioFinal -= precioFinal * 0.05;
        }

        Entrada entrada = new Entrada(contadorEntradas++, ubicacion, precioFinal, tipo, reserva);
        entradas.add(entrada);

        if (!reserva) {
            totalEntradasVendidas++;
            totalIngresos += precioFinal;
        }

        System.out.println((reserva ? "Entrada reservada: " : "Entrada comprada: ") + entrada);
    }
}