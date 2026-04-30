/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bank_europe;

import bank_europe.cliente.Cliente;
import bank_europe.cliente.InfoCliente;
import bank_europe.cuenta.CuentaBancaria;
import bank_europe.cuenta.intereses.CuentaAhorro;
import bank_europe.cuenta.intereses.CuentaDigital;
import bank_europe.cuenta.operaciones.CuentaCorriente;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Felip
 */
public class Bank_Europe {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Cliente> clientes = new ArrayList<>();

        while (true) {
            System.out.println("--- Menú Bank Europe ---");
            System.out.println("1. Registrar nuevo cliente");
            System.out.println("2. Mostrar datos de cliente");
            System.out.println("3. Depositar");
            System.out.println("4. Retirar");
            System.out.println("5. Eliminar cuenta");
            System.out.println("6. Consultar saldo e interés");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1" -> registrarCliente(sc, clientes);
                case "2" -> mostrarCliente(sc, clientes);
                case "3" -> operarDeposito(sc, clientes);
                case "4" -> operarRetiro(sc, clientes);
                case "5" -> eliminarCuenta(sc, clientes);
                case "6" -> consultarSaldoInteres(sc, clientes);
                case "7" -> {
                    System.out.println("¡Gracias por usar Bank Europe!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private static void registrarCliente(Scanner sc, List<Cliente> clientes) {
        System.out.print("RUT (12.345.789-1): ");
        String rut = sc.nextLine();
        if (!rut.matches("^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$")) {
            System.out.println("⚠ RUT inválido. Intente de nuevo.");
            return;
        }
        if (clientes.stream().anyMatch(c -> c.getRut().equals(rut))) {
            System.out.println("⚠ Cliente ya registrado con este RUT.");
            return;
        }

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        if (nombre.trim().isEmpty()) { System.out.println("⚠ Nombre vacío."); return; }

        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        if (apellido.trim().isEmpty()) { System.out.println("⚠ Apellido vacío."); return; }

        System.out.println("Tipos de cuenta: 1=Corriente, 2=Ahorros, 3=Digital");
        System.out.print("Seleccione tipo: ");
        String tipoStr = sc.nextLine();
        if (!tipoStr.matches("[1-3]")) { System.out.println("⚠ Opción inválida."); return; }
        int tipo = Integer.parseInt(tipoStr);

        System.out.print("Número de cuenta (9 dígitos): ");
        String numero = sc.nextLine();
        if (!numero.matches("\\d{9}")) { System.out.println("⚠ Número inválido."); return; }

        System.out.print("Saldo inicial: ");
        String saldoStr = sc.nextLine();
        double saldo;
        try { saldo = Double.parseDouble(saldoStr); }
        catch (NumberFormatException e) { System.out.println("⚠ Saldo inválido."); return; }

        CuentaBancaria cuenta = switch (tipo) {
            case 1 -> new CuentaCorriente(numero, saldo);
            case 2 -> new CuentaAhorro(numero, saldo);
            default -> new CuentaDigital(numero, saldo);
        };

        Cliente cliente = new Cliente(rut, nombre, apellido, cuenta);
        clientes.add(cliente);
        System.out.println("Cliente y cuenta registrados exitosamente.");
    }

    private static void mostrarCliente(Scanner sc, List<Cliente> clientes) {
        System.out.print("Ingrese RUT: ");
        String rut = sc.nextLine();
        clientes.stream().filter(c -> c.getRut().equals(rut))
                .findFirst()
                .ifPresentOrElse(InfoCliente::mostrarInformacionCliente,
                        () -> System.out.println("Cliente no encontrado."));
    }

    private static void operarDeposito(Scanner sc, List<Cliente> clientes) {
        System.out.print("Ingrese RUT: "); String rut = sc.nextLine();
        clientes.stream().filter(c -> c.getRut().equals(rut))
                .findFirst().ifPresentOrElse(c -> {
                    System.out.print("Monto a depositar: ");
                    double monto = Double.parseDouble(sc.nextLine());
                    c.getCuenta().depositar(monto);
                    System.out.println("Depósito exitoso. Saldo: " + c.getCuenta().getSaldo());
                }, () -> System.out.println("Cliente no encontrado."));
    }

    private static void operarRetiro(Scanner sc, List<Cliente> clientes) {
        System.out.print("Ingrese RUT: "); String rut = sc.nextLine();
        clientes.stream().filter(c -> c.getRut().equals(rut))
                .findFirst().ifPresentOrElse(c -> {
                    System.out.print("Monto a retirar: ");
                    double monto = Double.parseDouble(sc.nextLine());
                    c.getCuenta().retirar(monto);
                    System.out.println("Retiro exitoso. Saldo: " + c.getCuenta().getSaldo());
                }, () -> System.out.println("Cliente no encontrado."));
    }

    private static void eliminarCuenta(Scanner sc, List<Cliente> clientes) {
        System.out.print("Ingrese RUT: "); String rut = sc.nextLine();
        clientes.stream().filter(c -> c.getRut().equals(rut))
                .findFirst().ifPresentOrElse(c -> {
                    if (c.getCuenta() == null) {
                        System.out.println("No tiene cuenta que eliminar.");
                    } else if (c.getCuenta().getSaldo() < 0) {
                        System.out.println("No puede eliminar cuentas con deuda.");
                    } else {
                        //  eliminarCuenta en Cliente
                        c.setCuenta(null);
                        System.out.println("Cuenta eliminada exitosamente.");
                    }
                }, () -> System.out.println("Cliente no encontrado."));
    }

    private static void consultarSaldoInteres(Scanner sc, List<Cliente> clientes) {
        System.out.print("Ingrese RUT: "); String rut = sc.nextLine();
        clientes.stream().filter(c -> c.getRut().equals(rut))
                .findFirst().ifPresentOrElse(c -> {
                    if (c.getCuenta() == null) {
                        System.out.println("Sin cuenta asignada.");
                    } else {
                        System.out.println("Saldo: " + c.getCuenta().getSaldo());
                        System.out.println("Interés estimado: " + c.getCuenta().calcularInteres());
                    }
                }, () -> System.out.println("Cliente no encontrado."));
    }
}