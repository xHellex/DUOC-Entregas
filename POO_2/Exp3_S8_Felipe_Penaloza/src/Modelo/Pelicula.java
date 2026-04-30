/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.Objects;

public final class Pelicula {
    private Integer id;       // puede venir null al insertar (AI)
    private String titulo;
    private String director;
    private int ano;
    private int duracion;     // minutos
    private String genero;

    public Pelicula(String titulo, String director, int ano, int duracion, String genero) {
        setTitulo(titulo);
        setDirector(director);
        setAno(ano);
        setDuracion(duracion);
        setGenero(genero);
    }

    public Pelicula(Integer id, String titulo, String director, int ano, int duracion, String genero) {
        this(titulo, director, ano, duracion, genero);
        this.id = id;
    }

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDirector() { return director; }
    public int getAno() { return ano; }
    public int getDuracion() { return duracion; }
    public String getGenero() { return genero; }

    public void setId(Integer id) { this.id = id; }
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) throw new IllegalArgumentException("Titulo requerido");
        this.titulo = titulo.trim();
    }
    public void setDirector(String director) {
        if (director == null || director.trim().isEmpty()) throw new IllegalArgumentException("Director requerido");
        this.director = director.trim();
    }
    public void setAno(int ano) {
        if (ano < 1880 || ano > 2100) throw new IllegalArgumentException("Año inválido");
        this.ano = ano;
    }
    public void setDuracion(int duracion) {
        if (duracion <= 0) throw new IllegalArgumentException("Duración debe ser positiva");
        this.duracion = duracion;
    }
    public void setGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) throw new IllegalArgumentException("Género requerido");
        this.genero = genero.trim();
    }

    @Override
    public String toString() {
        return "Pelicula{id=" + id + ", titulo='" + titulo + "', director='" + director +
                "', ano=" + ano + ", duracion=" + duracion + ", genero='" + genero + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pelicula)) return false;
        Pelicula pelicula = (Pelicula) o;
        return Objects.equals(id, pelicula.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}