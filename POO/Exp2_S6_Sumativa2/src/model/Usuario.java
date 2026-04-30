/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import exceptions.InvalidDataException;
import java.util.Objects;

/**
 *
 * @author Felip
 */
public class Usuario implements Comparable<Usuario> {
    private String email;
    private String nombre;

    public Usuario(String email, String nombre) {
        if (email == null || !email.matches(".+@.+\\..+"))
            throw new InvalidDataException("Email inválido");
        if (nombre == null || nombre.isBlank())
            throw new InvalidDataException("Nombre inválido");
        this.email = email;
        this.nombre = nombre;
    }

    public String getEmail()  { return email; }
    public String getNombre() { return nombre; }

    public void mostrarInfo() {
        System.out.printf("%s [%s]%n", nombre, email);
    }

    @Override
    public int compareTo(Usuario o) {
        return this.nombre.compareToIgnoreCase(o.nombre);
    }
    @Override public boolean equals(Object o) {
        return (o instanceof Usuario) && 
               email.equalsIgnoreCase(((Usuario)o).email);
    }
    @Override public int hashCode() {
        return Objects.hash(email.toLowerCase());
    }
}