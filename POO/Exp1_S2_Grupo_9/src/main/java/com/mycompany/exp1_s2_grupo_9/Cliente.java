/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.exp1_s2_grupo_9;

import java.util.regex.Pattern;
/**
 *
 * @author Felip
 */
/**
 * Clase Cliente que implementa la interfaz Mostrable.
 */
public class Cliente implements Interfaz {
    private String rut, nombre, apellidoPaterno, apellidoMaterno, domicilio, comuna, telefono;
    private Cuenta cuenta;

    public Cliente(String rut, String nombre, String apellidoPaterno, String apellidoMaterno,
                   String domicilio, String comuna, String telefono, Cuenta cuenta) {
        validarRut(rut);
        validarTexto(nombre, "nombre");
        validarTexto(apellidoPaterno, "apellido paterno");
        validarTexto(apellidoMaterno, "apellido materno");
        if (domicilio == null || domicilio.trim().isEmpty()) throw new IllegalArgumentException("Domicilio no puede estar vacío");
        if (comuna == null || comuna.trim().isEmpty()) throw new IllegalArgumentException("Comuna no puede estar vacía");
        if (!telefono.matches("\\d{7,15}")) throw new IllegalArgumentException("Teléfono inválido: solo números (7 a 15 dígitos)");
        this.rut = rut;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.domicilio = domicilio;
        this.comuna = comuna;
        this.telefono = telefono;
        this.cuenta = cuenta;
    }

    private void validarRut(String rut) {
        if (!Pattern.matches("\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]", rut)) {
            throw new IllegalArgumentException("RUT inválido: formato debe ser 12.345.678-9");
        }
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty() || !texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException(campo + " inválido: solo letras y no vacío");
        }
    }

    public Cuenta getCuenta() { return cuenta; }
    public String getRut() { return rut; }

    @Override
    public void mostrarInfoCliente() {
        System.out.println("RUT: " + rut);
        System.out.println("Nombre: " + nombre + " " + apellidoPaterno + " " + apellidoMaterno);
        System.out.println("Domicilio: " + domicilio + ", Comuna: " + comuna);
        System.out.println("Teléfono: " + telefono);
        if (cuenta != null) {
            System.out.println("Tipo de cuenta: " + cuenta.obtenerTipoCuenta());
            System.out.println("Número de cuenta: " + cuenta.getNumero());
            System.out.println("Saldo actual: $" + cuenta.getSaldo());
            if (cuenta instanceof CuentaCredito) {
                System.out.println("Límite de crédito: $" + ((CuentaCredito) cuenta).getLimiteCredito());
            }
        }
    }
}
