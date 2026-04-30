/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import exceptions.DatosInvalidosException;
import java.util.Objects;

/**
 *
 * @author Felipe Peñaloza & Joaquín Gomez
 */
public class Usuario implements Comparable<Usuario> {
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
        if (!telefono.matches("\\d{7,15}"))
            throw new DatosInvalidosException("Teléfono inválido");
        this.sede = sede; this.rut = rut;
        this.nombre = nombre; this.apellidoPat = apellidoPat;
        this.apellidoMat = apellidoMat; this.telefono = telefono;
    }

    private void validarTexto(String texto, String campo) {
        if (texto==null||texto.trim().isEmpty()||!texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))
            throw new DatosInvalidosException(campo+" inválido");
    }

    public String getRut() { return rut; }
    public String getNombreCompleto() { return nombre+" "+apellidoPat+" "+apellidoMat; }

    public void mostrarInformacionUsuario() {
        System.out.println("Sede: " + sede);
        System.out.println("RUT: " + rut);
        System.out.println("Nombre Completo: " + getNombreCompleto());
        System.out.println("Teléfono: " + telefono);
    }

    // Comparador para TreeSet orden alfabético por nombre completo
    @Override public int compareTo(Usuario o) {
        return this.getNombreCompleto().compareToIgnoreCase(o.getNombreCompleto());
    }
    @Override public boolean equals(Object o) {
        if (!(o instanceof Usuario)) return false;
        return Objects.equals(rut, ((Usuario)o).rut);
    }
    @Override public int hashCode() { return Objects.hash(rut); }
}
