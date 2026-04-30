/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bank_europe.cliente;

import bank_europe.cuenta.CuentaBancaria;
import java.util.regex.Pattern;
/**
 *
 * @author Felip
 */
public class Cliente implements InfoCliente {
    private String rut;
    private String nombre;
    private String apellido;
    private CuentaBancaria cuenta;

    public Cliente(String rut, String nombre, String apellido, CuentaBancaria cuenta) {
        // Validación de RUT: formato 12.345.789-1
        if (rut == null || !rut.matches("^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$")) {
            throw new IllegalArgumentException("RUT inválido: formato debe ser 12.345.789-1");
        }
        this.rut = rut;

        // Validación de nombre y apellido antes de asignar
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("Apellido no puede estar vacío");
        }
        this.nombre = nombre;
        this.apellido = apellido;

        this.cuenta = cuenta;
    }

    public String getRut() {
        return rut;
    }

    public CuentaBancaria getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public void mostrarInformacionCliente() {
        System.out.println("RUT: " + rut);
        System.out.println("Nombre: " + nombre + " " + apellido);
        if (cuenta != null) {
            System.out.println("Cuenta: " + cuenta.getNumeroCuenta());
            System.out.println("Saldo: " + cuenta.getSaldo());
            System.out.println("Interés estimado: " + cuenta.calcularInteres());
        } else {
            System.out.println("Sin cuenta asignada.");
        }
    }
}