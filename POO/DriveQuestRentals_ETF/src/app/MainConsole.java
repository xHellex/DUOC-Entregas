/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import concurrency.VehicleLoader;
import io.VehicleWriter;
import java.util.List;
import java.util.Scanner;
import model.CargoVehicle;
import model.PassengerVehicle;
import model.Vehicle;
import service.Billing;
import service.FleetManager;

/**
 *
 * @author Felip
 */
public class MainConsole {
    private static final String DATA_FILE = "vehicles.csv";
    private final FleetManager mgr = new FleetManager();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        MainConsole app = new MainConsole();
        try (app.sc) {
            app.loadDataConcurrently();
            app.menuLoop();
        } finally {
            System.out.println("¡Hasta luego!");
        }
    }

    private void loadDataConcurrently() {
        Thread loader = new VehicleLoader(DATA_FILE, mgr);
        loader.start();
        try {
            loader.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("Carga interrumpida");
        }
        System.out.println("Datos cargados: " + mgr.listAll().size() + " vehículos.");
    }

    private void menuLoop() {
        while (true) {
            System.out.println("\n--- DriveQuest Rentals ---");
            System.out.println("1) Agregar vehículo");
            System.out.println("2) Listar todos");
            System.out.println("3) Listar arriendos ≥ 7 días");
            System.out.println("4) Mostrar boleta");
            System.out.println("0) Salir");
            System.out.print("Elige opción: ");
            String input = sc.nextLine().trim();
            switch (input) {
                case "1" -> addVehicle();
                case "2" -> listAll();
                case "3" -> listLong();
                case "4" -> showBilling();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void addVehicle() {
        try {
            System.out.print("Tipo (C/P): ");
            String type = sc.nextLine().trim().toUpperCase();
            System.out.print("Patente: ");
            String plate = sc.nextLine().trim();
            System.out.print("Marca: ");
            String brand = sc.nextLine().trim();
            System.out.print("Modelo: ");
            String model = sc.nextLine().trim();
            System.out.print("Días: ");
            int days = Integer.parseInt(sc.nextLine().trim());

            Vehicle v = null;
            boolean valid = false;

            while (!valid) {
                try {
                    if (null == type) {
                        System.err.println("Tipo no válido. Debe ser 'C' o 'P'.");
                        return;
                    } else switch (type) {
                        case "C" -> {
                            System.out.print("Capacidad de carga (en toneladas): ");
                            double cap = Double.parseDouble(sc.nextLine().trim());
                            v = new CargoVehicle(plate, brand, model, days, cap);
                        }
                        case "P" -> {
                            System.out.print("Número de pasajeros: ");
                            int pax = Integer.parseInt(sc.nextLine().trim());
                            v = new PassengerVehicle(plate, brand, model, days, pax);
                        }
                        default -> {
                            System.err.println("Tipo no válido. Debe ser 'C' o 'P'.");
                            return;
                        }
                    }
                    valid = true;
                } catch (NumberFormatException ex) {
                    System.err.println("❌ Entrada inválida. Intenta nuevamente.");
                }
            }

            mgr.addVehicle(v);
            VehicleWriter.save(DATA_FILE, v);
            System.out.println("Vehículo agregado.");
        } catch (NumberFormatException ex) {
            System.err.println("❌ Entrada numérica inválida: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("❌ Error de validación: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("❌ Error inesperado: " + ex.getMessage());
        }
    }

    private void listAll() {
        List<Vehicle> all = mgr.listAll();
        if (all.isEmpty()) System.out.println("No hay vehículos.");
        else all.forEach(v -> System.out.println(v.showInfo()));
    }

    private void listLong() {
        List<Vehicle> lt = mgr.longTerm();
        if (lt.isEmpty()) System.out.println("No hay arriendos ≥7 días.");
        else lt.forEach(v -> System.out.println(v.showInfo()));
    }

    private void showBilling() {
        try {
            System.out.print("Patente: ");
            String plate = sc.nextLine().trim();
            Vehicle v = mgr.listAll().stream()
                .filter(x -> x.getPlate().equalsIgnoreCase(plate))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No encontrado: " + plate));

            System.out.print("Tarifa diaria: ");
            double rate = Double.parseDouble(sc.nextLine().trim());
            double subtotal = rate * v.getRentDays();
            double total = ((Billing)mgr).calculate(v, rate);
            double ivaAmt = subtotal * Billing.IVA;
            double discAmt = subtotal * (v instanceof CargoVehicle ? Billing.DISCOUNT_CARGO : Billing.DISCOUNT_PASSENGER);
            System.out.printf("Subtotal=%.2f, IVA=%.2f, Desc=%.2f, Total=%.2f%n",
                subtotal, ivaAmt, discAmt, total);
        } catch (NumberFormatException ex) {
            System.err.println("Tarifa no válida: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
}
