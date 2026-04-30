/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import exceptions.BusinessException;
import exceptions.ComicNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import model.Comic;
import model.Usuario;

/**
 *
 * @author Felip
 */
public class ComicCollectorService {
    // Colecciones centrales
    private List<Comic> listaComics    = new ArrayList<>();
    private Map<String,Usuario> usuarios = new HashMap<>();
    private Set<Comic> setComics        = new HashSet<>();
    private Set<Comic> treeComics       = new TreeSet<>();
    private Set<Usuario> treeUsuarios   = new TreeSet<>();

    /** Carga comics desde CSV (data/comics.csv) */
    public void cargarComicsCsv(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                if (p.length != 3) continue;
                try {
                    Comic c = new Comic(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()));
                    if (setComics.add(c)) {
                        listaComics.add(c);
                        treeComics.add(c);
                    }
                } catch (IllegalArgumentException ex) {
                    System.err.println("Línea inválida: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error lectura CSV: " + e.getMessage());
        }
    }

    /** Registra un usuario y persiste en data/users.txt */
    public void registrarUsuario(Usuario u) throws BusinessException {
        if (usuarios.containsKey(u.getEmail()))
            throw new BusinessException("Usuario ya existe");
        usuarios.put(u.getEmail(), u);
        treeUsuarios.add(u);
        guardarUsuariosTxt("data/usuarios.txt");
    }

    /** Guarda todos los usuarios en un TXT plano */
    private void guardarUsuariosTxt(String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Usuario u : usuarios.values())
                bw.write(u.getEmail() + "," + u.getNombre() + "\n");
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    /** Reserva un cómic dado su título#número */
    public void reservarComic(String titulo, int numero) throws BusinessException {
        Comic buscado = listaComics.stream()
            .filter(c -> c.getTitulo().equalsIgnoreCase(titulo) && c.getNumero()==numero)
            .findFirst()
            .orElseThrow(() -> new ComicNotFoundException("No existe: " + titulo + "#" + numero));
        if (buscado.isReservado())
            throw new BusinessException("Ya está reservado");
        buscado.reservar();
    }

    /** Muestra todos los cómics (ArrayList) */
    public void listarComics() {
        listaComics.forEach(Comic::mostrarInfo);
    }

    /** Muestra catálogo ordenado (TreeSet) */
    public void mostrarCatalogoOrdenado() {
        treeComics.forEach(Comic::mostrarInfo);
    }

    /** Muestra todos los usuarios */
    public void listarUsuarios() {
        treeUsuarios.forEach(Usuario::mostrarInfo);
    }
}