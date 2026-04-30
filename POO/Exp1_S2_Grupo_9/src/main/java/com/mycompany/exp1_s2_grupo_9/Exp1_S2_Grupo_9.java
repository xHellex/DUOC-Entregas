/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
   Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java
 */

package com.mycompany.exp1_s2_grupo_9;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Felip
 */

/**
 * Clase principal con menú interactivo, aplicando herencia, polimorfismo e interfaz. 
 */
public class Exp1_S2_Grupo_9 {

     public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Cliente> clientes = new ArrayList<>();

        while (true) {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Registrar nuevo cliente");
            System.out.println("2. Ver datos de un cliente");
            System.out.println("3. Depositar");
            System.out.println("4. Girar");
            System.out.println("5. Consultar saldo");
System.out.println("6. Salir");
            int opcion = -1;
while (true) {
    System.out.print("Seleccione una opción: ");
    String entrada = sc.nextLine();
    if (entrada.matches("[1-6]")) {
        opcion = Integer.parseInt(entrada);
        break;
    } else {
        System.out.println("⚠ Ingrese solo un número del 1 al 6.");
    }
}

            try {
                switch (opcion) {
                    case 1 -> {
                        System.out.print("RUT (formato 12.345.678-9): ");
String rut = sc.nextLine();
if (clientes.stream().anyMatch(cli -> cli.getRut().equals(rut))) {
    System.out.println("⚠ Ya existe un cliente registrado con este RUT.");
    break;
}
                        System.out.print("Nombre: ");
                        String nombre = sc.nextLine();
                        System.out.print("Apellido Paterno: ");
                        String ap = sc.nextLine();
                        System.out.print("Apellido Materno: ");
                        String am = sc.nextLine();
                        System.out.print("Domicilio: ");
                        String dom = sc.nextLine();
                        System.out.print("Comuna: ");
                        String com = sc.nextLine();
                        System.out.print("Teléfono: ");
                        String tel = sc.nextLine();

                        System.out.println("Seleccione tipo de cuenta: 1. Corriente | 2. Ahorro | 3. Crédito");
                        int tipo = sc.nextInt(); sc.nextLine();
                        System.out.print("Número de cuenta (9 dígitos): ");
                        String num = sc.nextLine();
                        Cuenta cuenta = switch (tipo) {
                            case 1 -> new CuentaCorriente(num, 0);
                            case 2 -> new CuentaAhorro(num);
                            case 3 -> {
                                System.out.print("Límite de crédito: ");
                                double limite = sc.nextDouble(); sc.nextLine();
                                yield new CuentaCredito(num, limite);
                            }
                            default -> throw new IllegalArgumentException("Tipo de cuenta inválido");
                        };

                        Cliente nuevo = new Cliente(rut, nombre, ap, am, dom, com, tel, cuenta);
                        clientes.add(nuevo);
                        System.out.println("Cliente registrado exitosamente.");
                    }
                    case 2 -> {
                        if (clientes.isEmpty()) {
                            System.out.println("No hay clientes registrados.");
                            break;
                        }
                        System.out.print("Ingrese RUT del cliente: ");
                        String rutBusqueda = sc.nextLine();
                        Cliente c = clientes.stream().filter(cli -> cli.getRut().equals(rutBusqueda)).findFirst().orElse(null);
                        if (c != null) c.mostrarInfoCliente();
                        else System.out.println("Cliente no encontrado.");
                    }
                    case 3 -> {
                        System.out.print("Ingrese RUT del cliente: ");
                        String rutBusqueda = sc.nextLine();
                        Cliente c = clientes.stream().filter(cli -> cli.getRut().equals(rutBusqueda)).findFirst().orElse(null);
                        if (c == null) {
                            System.out.println("Cliente no encontrado.");
                            break;
                        }
                        System.out.print("Monto a depositar: ");
                        double monto = sc.nextDouble(); sc.nextLine();
                        c.getCuenta().depositar(monto);
                        System.out.println("Depósito exitoso. Saldo: $" + c.getCuenta().getSaldo());
                    }
                    case 4 -> {
                        System.out.print("Ingrese RUT del cliente: ");
                        String rutBusqueda = sc.nextLine();
                        Cliente c = clientes.stream().filter(cli -> cli.getRut().equals(rutBusqueda)).findFirst().orElse(null);
                        if (c == null) {
                            System.out.println("Cliente no encontrado.");
                            break;
                        }
                        System.out.print("Monto a girar: ");
                        double monto = sc.nextDouble(); sc.nextLine();
                        c.getCuenta().retirar(monto);
                        System.out.println("Giro exitoso. Saldo: $" + c.getCuenta().getSaldo());
                    }
                    case 5 -> {
    System.out.print("Ingrese RUT del cliente: ");
    String rutBusqueda = sc.nextLine();
    Cliente c = clientes.stream().filter(cli -> cli.getRut().equals(rutBusqueda)).findFirst().orElse(null);
    if (c == null) {
        System.out.println("Cliente no encontrado.");
    } else {
        System.out.println("Saldo actual: $" + c.getCuenta().getSaldo());
    }
}
case 6 -> {
    System.out.println("Gracias por usar Bank Boston.");
    return;
}
                    default -> System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
