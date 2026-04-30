/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.Objects;

public final class Cliente {
    private String rut;
    private String nombre;
    private String direccion;
    private String comuna;
    private String email;
    private String telefono;

    public Cliente(String rut, String nombre, String direccion, String comuna, String email, String telefono) {
        setRut(rut);
        setNombre(nombre);
        setDireccion(direccion);
        setComuna(comuna);
        setEmail(email);
        setTelefono(telefono);
    }

    public String getRut() { return rut; }
    public void setRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) throw new IllegalArgumentException("RUT requerido");
        this.rut = rut.trim();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("Nombre requerido");
        this.nombre = nombre.trim();
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = (direccion == null) ? "" : direccion.trim(); }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = (comuna == null) ? "" : comuna.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email requerido");
        this.email = email.trim();
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = (telefono == null) ? "" : telefono.trim(); }

    @Override
    public String toString() {
        return rut + " - " + nombre + " (" + email + ") " + telefono;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(rut, cliente.rut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rut);
    }
}
