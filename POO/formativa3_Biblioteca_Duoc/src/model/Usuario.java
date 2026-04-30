/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import exceptions.DatosInvalidosException;

/**
 *
 * @author Felipe Peñaloza & Joaquín Gomez
 */
public class Usuario {
    private String sede;
    private String rut;
    private String nombre;
    private String apellidoPat;
    private String apellidoMat;
    private String telefono;

    public Usuario(String sede, String rut, String nombre,
                   String apellidoPat, String apellidoMat,
                   String telefono) {
        validarTexto(sede, "sede");
        validarTexto(nombre, "nombre");
        validarTexto(apellidoPat, "apellido paterno");
        validarTexto(apellidoMat, "apellido materno");
        if (!telefono.matches("\\d{7,15}")) {
            throw new DatosInvalidosException(
                "Teléfono inválido: solo números (7 a 15 dígitos)");
        }
        this.sede = sede;
        this.rut = rut;
        this.nombre = nombre;
        this.apellidoPat = apellidoPat;
        this.apellidoMat = apellidoMat;
        this.telefono = telefono;
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()
            || !texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new DatosInvalidosException(
                campo + " inválido: solo letras y no vacío");
        }
    }

    public String getRut() { return rut; }
    public void mostrarInformacionUsuario() {
        System.out.println("Sede: " + sede);
        System.out.println("RUT: " + rut);
        System.out.println("Nombre Completo: "
            + nombre + " " + apellidoPat + " " + apellidoMat);
        System.out.println("Teléfono: " + telefono);
    }
}

