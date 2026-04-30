/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;

/**
 *
 * @author Felip
 */
public class Comic implements Comparable<Comic> {
    private String titulo;
    private String editorial;
    private int numero;
    private boolean reservado;

    public Comic(String titulo, String editorial, int numero) {
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Título inválido");
        if (editorial == null || editorial.isBlank()) throw new IllegalArgumentException("Editorial inválida");
        if (numero <= 0) throw new IllegalArgumentException("Número debe ser > 0");
        this.titulo = titulo;
        this.editorial = editorial;
        this.numero = numero;
        this.reservado = false;
    }

    public String getTitulo() { return titulo; }
    public int getNumero()     { return numero; }
    public boolean isReservado() { return reservado; }

    public void reservar() { this.reservado = true; }
    public void liberar()  { this.reservado = false; }

    public void mostrarInfo() {
        System.out.printf("%s #%d (%s) - %s%n",
            titulo, numero, editorial,
            reservado ? "RESERVADO" : "DISPONIBLE");
    }

    @Override
    public int compareTo(Comic o) {
        int cmp = this.titulo.compareToIgnoreCase(o.titulo);
        return (cmp != 0) ? cmp : Integer.compare(this.numero, o.numero);
    }
    @Override public boolean equals(Object o) {
        if (!(o instanceof Comic)) return false;
        Comic c = (Comic)o;
        return titulo.equalsIgnoreCase(c.titulo) && numero == c.numero;
    }
    @Override public int hashCode() {
        return Objects.hash(titulo.toLowerCase(), numero);
    }
}