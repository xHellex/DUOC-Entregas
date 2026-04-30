/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.exp1_s1_grupo_9;

import java.util.regex.Pattern;
/**
 *
 * @author Felip
 */
public class Cliente {
    //encapsular
    private String rut, nombre, apellidoPaterno, apellidoMaterno, domicilio, comuna, telefono;
    private CuentaCorriente cuenta;

    //  - Constructores -
    // Constructor vacio para leer
    public Cliente() {
    }
    // Constructor con datos
    public Cliente(String rut, String nombre, String apellidoPaterno, String apellidoMaterno,
                   String domicilio, String comuna, String telefono, String numeroCuenta) {
        if (!Pattern.matches("\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]", rut)) {
            throw new IllegalArgumentException("RUT inválido: debe tener el formato 12345678-9");
        }
        if (!Pattern.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+", nombre)){
            throw new IllegalArgumentException("Nombre inválido: solo se permiten letras");
        }
        if (!Pattern.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+", apellidoPaterno)) {
            throw new IllegalArgumentException("Apellido paterno inválido: solo se permiten letras");
        }
        if (!Pattern.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+", apellidoMaterno)) {
            throw new IllegalArgumentException("Apellido materno inválido: solo se permiten letras");
        }
        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new IllegalArgumentException("Domicilio no puede estar vacío");
        }
        if (comuna == null || comuna.trim().isEmpty()) {
            throw new IllegalArgumentException("Comuna no puede estar vacía");
        }
        if (!Pattern.matches("\\d{7,15}", telefono)) {
            throw new IllegalArgumentException("Teléfono inválido: solo se permiten números de 7 a 15 dígitos");
        }
        this.rut = rut;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.domicilio = domicilio;
        this.comuna = comuna;
        this.telefono = telefono;
        this.cuenta = new CuentaCorriente(numeroCuenta);
    }

    // Getter & Setter
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPat() {
        return apellidoPaterno;
    }

    public void setApellidoPat(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public CuentaCorriente getCuenta() {
        return cuenta;
    }

    public void setCuenta(String numeroCuenta) {
    this.cuenta = new CuentaCorriente(numeroCuenta);
}

    //Vista Developer

    @Override
    public String toString() {
        return "Cliente{" + "rut=" + rut + ", nombre=" + nombre + ", apellidoPat=" + apellidoPaterno + ", apellidoMat=" + apellidoMaterno + ", domicilio=" + domicilio + ", comuna=" + comuna + ", telefono=" + telefono + ", cuenta=" + cuenta + '}';
    }

    //Metodos propios

    public void mostrarDatos() {
        System.out.println("RUT: " + rut);
        System.out.println("Nombre: " + nombre + " " + apellidoPaterno + " " + apellidoMaterno);
        System.out.println("Domicilio: " + domicilio + ", Comuna: " + comuna);
        System.out.println("Teléfono: " + telefono);
        System.out.println("Cuenta corriente N°: " + cuenta.getNumero());
        System.out.println("Saldo actual: " + cuenta.getSaldo() + " pesos");
    }

}
