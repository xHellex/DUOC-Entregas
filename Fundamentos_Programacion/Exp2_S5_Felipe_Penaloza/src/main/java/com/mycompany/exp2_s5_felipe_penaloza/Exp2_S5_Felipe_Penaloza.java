/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
   Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java
 */

package com.mycompany.exp2_s5_felipe_penaloza;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Felip
 */
public class Exp2_S5_Felipe_Penaloza {

    // Variables estáticas (estadísticas globales)
    static int totalEntradasVendidas = 0;
    static double totalIngresos = 0;
    static final String nombreTeatro = "Teatro Moro";

    // Clase interna para representar una entrada
    static class Entrada {
        int numero;
        String ubicacion;
        double precioFinal;
        String tipoCliente;

        Entrada(int numero, String ubicacion, double precioFinal, String tipoCliente) {
            this.numero = numero;
            this.ubicacion = ubicacion;
            this.precioFinal = precioFinal;
            this.tipoCliente = tipoCliente;
        }

        @Override
        public String toString() {
            return "N°: " + numero + ", Ubicación: " + ubicacion + ", Precio: $" + precioFinal + ", Tipo: " + tipoCliente;
        }
    }

    // Lista para almacenar entradas vendidas (variable de instancia simulada como estática aquí)
    static List<Entrada> entradasVendidas = new ArrayList<>();
    static int contadorEntradas = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Variables locales generales
        int capacidadSala = 100;
        int entradasDisponibles = capacidadSala;
        double precioVIP = 20000;
        double precioPlatea = 15000;
        double precioGeneral = 10000;

        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== SISTEMA VENTA ENTRADAS - " + nombreTeatro + " =====");
            System.out.println("1. Venta de entradas");
            System.out.println("2. Ver promociones");
            System.out.println("3. Buscar entrada");
            System.out.println("4. Eliminar entrada");
            System.out.println("5. Ver estadísticas");
            System.out.println("6. Salir");

            int opcion = -1;
            while (true) {
                System.out.print("Seleccione una opción: ");
                if (sc.hasNextInt()) {
                    opcion = sc.nextInt();
                    sc.nextLine(); // limpiar buffer
                    break;
                } else {
                    System.out.println("Entrada inválida. Por favor, ingrese un número del 1 al 6.");
                    sc.nextLine();
                }
            }

            switch (opcion) {
                case 1 -> {
                    if (entradasDisponibles <= 0) {
                        System.out.println("No hay entradas disponibles.");
                        break;
                    }

                    String ubicacion = "";
                    String tipoCliente;
                    double precioBase = 0;
                    double descuento = 0;
                    double precioFinal;

                    String[] ubicacionesValidas = {"vip", "platea", "general"};

                    while (true) {
                        System.out.print("Ingrese ubicación (VIP, Platea, General): ");
                        ubicacion = sc.nextLine().toLowerCase();
                        boolean valida = false;
                        for (String u : ubicacionesValidas) {
                            if (u.equals(ubicacion)) {
                                valida = true;
                                break;
                            }
                        }
                        if (valida) break;
                        System.out.println("Ubicación inválida. Debe ser VIP, Platea o General.");
                    }

                    switch (ubicacion) {
                        case "vip" -> precioBase = precioVIP;
                        case "platea" -> precioPlatea;
                        case "general" -> precioBase = precioGeneral;
                    }

                    System.out.print("Tipo de cliente (Normal, Estudiante, TerceraEdad): ");
                    tipoCliente = sc.nextLine().toLowerCase();

                    if (tipoCliente.equals("estudiante")) {
                        descuento = 0.10;
                    } else if (tipoCliente.equals("terceraedad")) {
                        descuento = 0.15;
                    }

                    precioFinal = precioBase - (precioBase * descuento);
                    Entrada nuevaEntrada = new Entrada(contadorEntradas++, ubicacion, precioFinal, tipoCliente);
                    entradasVendidas.add(nuevaEntrada);
                    entradasDisponibles--;
                    totalEntradasVendidas++;
                    totalIngresos += precioFinal;

                    System.out.println("Entrada vendida con éxito:");
                    System.out.println(nuevaEntrada);
                }

                case 2 -> {
                    System.out.println("\n--- PROMOCIONES DISPONIBLES ---");
                    System.out.println("* 10% de descuento para estudiantes.");
                    System.out.println("* 15% de descuento para personas de la tercera edad.");
                    System.out.println("* Compra de 3 entradas o más: +5% de descuento adicional.");
                }

                case 3 -> {
                    System.out.print("Buscar por (numero/ubicacion/tipo): ");
                    String criterio = sc.nextLine().toLowerCase();
                    System.out.print("Ingrese el valor de búsqueda: ");
                    String valorBusqueda = sc.nextLine().toLowerCase();

                    boolean encontrado = false;
                    for (Entrada entrada : entradasVendidas) {
                        if ((criterio.equals("numero") && Integer.toString(entrada.numero).equals(valorBusqueda)) ||
                                (criterio.equals("ubicacion") && entrada.ubicacion.equalsIgnoreCase(valorBusqueda)) ||
                                (criterio.equals("tipo") && entrada.tipoCliente.equalsIgnoreCase(valorBusqueda))) {
                            System.out.println(entrada);
                            encontrado = true;
                        }
                    }

                    if (!encontrado) {
                        System.out.println("No se encontró ninguna entrada con ese criterio.");
                    }
                }

                case 4 -> {
                    int numeroEliminar;
                    while (true) {
                        System.out.print("Ingrese número de entrada a eliminar: ");
                        if (sc.hasNextInt()) {
                            numeroEliminar = sc.nextInt();
                            sc.nextLine();
                            break;
                        } else {
                            System.out.println("Debe ingresar un número válido.");
                            sc.nextLine();
                        }
                    }

                    Iterator<Entrada> it = entradasVendidas.iterator();
                    boolean eliminado = false;
                    while (it.hasNext()) {
                        Entrada e = it.next();
                        if (e.numero == numeroEliminar) {
                            totalIngresos -= e.precioFinal;
                            totalEntradasVendidas--;
                            entradasDisponibles++;
                            it.remove();
                            eliminado = true;
                            System.out.println("Entrada eliminada con éxito.");
                            break;
                        }
                    }
                    if (!eliminado) {
                        System.out.println("No se encontró la entrada.");
                    }
                }

                case 5 -> {
                    System.out.println("--- ESTADÍSTICAS ---");
                    System.out.println("Entradas vendidas: " + totalEntradasVendidas);
                    System.out.println("Entradas disponibles: " + entradasDisponibles);
                    System.out.println("Ingresos totales: $" + totalIngresos);
                }

                case 6 -> {
                    salir = true;
                    System.out.println("Gracias por utilizar el sistema.");
                }

                default -> System.out.println("Opción no válida.");
            }
        }
    }
}
