/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Felipe Peñaloza & Joaquín Gomez
 */
public class Libro {
    private String titulo;
    private String autor;
    private boolean disponible;

    public Libro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = true;
    }

    public boolean estaDisponible() { return disponible; }
    public void prestar() { disponible = false; }
    public void devolver() { disponible = true; }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }

    public void mostrarInformacion() {
        System.out.println("Título: " + titulo);
        System.out.println("Autor: " + autor);
        System.out.println("Estado: " + (disponible ? "Disponible" : "Prestado"));
    }
}
