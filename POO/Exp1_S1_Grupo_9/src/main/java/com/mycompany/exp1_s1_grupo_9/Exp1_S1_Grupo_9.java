/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
   Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java
 */

package com.mycompany.exp1_s1_grupo_9;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Felip
 */
public class Exp1_S1_Grupo_9 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Cliente cliente = null;
        int opcion;

        do {
            System.out.println("\n--- Bank Boston ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Ver datos de cliente");
            System.out.println("3. Depositar");
            System.out.println("4. Girar");
            System.out.println("5. Consultar saldo");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = sc.nextInt(); sc.nextLine();
            try {
                switch (opcion) {
                    case 1 -> {
                        String rut;
                        while (true) {
                            System.out.print("Ingrese Rut (formato 12.345.678-9): ");
                            rut = sc.nextLine();
                            if (Pattern.matches("\
{1,2}\.\
{3}\.\
{3}-[\
kK]", rut)) break;
                            else System.out.println("Error: RUT inválido. Intente nuevamente.");
                        }
                        // Validaciones para cada campo restantes igual que antes...
                        // Se omite por brevedad

                        // Entrada de monto con validación
                        int monto;
                        while (true) {
                            System.out.print("Ingrese monto a depositar: ");
                            if (sc.hasNextInt()) {
                                monto = sc.nextInt(); sc.nextLine();
                                if (monto > 0) break;
                                else System.out.println("⚠ El monto debe ser mayor a 0.");
                            } else {
                                System.out.println("⚠ Debe ingresar un número válido.");
                                sc.next();
                            }
                        }
                        // Aplicar a giro también de forma similar
                    }
                    // ... resto del menú sin cambios esenciales
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } while (opcion != 6);

        sc.close();
    }
}
