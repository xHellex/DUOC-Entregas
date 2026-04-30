/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Conexion.DatabaseConnection;
import Modelo.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * author Joaquín Gómez Flores & Felipe Peñaloza
 * DAO para la tabla Cartelera.
 * - Usa PreparedStatement para consultas seguras.
 * - Métodos claros: listarPeliculas, buscarPorGenero, agregarPelicula, buscarPorTitulo, actualizarPelicula, eliminarPelicula.
 * - Incluye método para insertar múltiples películas dentro de una transacción.
 */
public class CarteleraDAO {

    // LISTAR TODOS
    public List<Pelicula> listarPeliculas() throws Exception {
        String sql = "SELECT id, titulo, director, ano, duracion, genero, creado_at FROM Cartelera ORDER BY id DESC";
        List<Pelicula> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // BUSCAR POR GENERO
    public List<Pelicula> buscarPorGenero(String genero) throws Exception {
        String sql = "SELECT id, titulo, director, ano, duracion, genero, creado_at FROM Cartelera WHERE genero = ? ORDER BY id DESC";
        List<Pelicula> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genero);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    // BUSCAR POR ID
    public Pelicula buscarPorId(int id) throws Exception {
        String sql = "SELECT id, titulo, director, ano, duracion, genero, creado_at FROM Cartelera WHERE id = ?";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // BUSCAR POR TITULO (exacto, case-insensitive)
    public Pelicula buscarPorTitulo(String titulo) throws Exception {
        String sql = "SELECT id, titulo, director, ano, duracion, genero, creado_at FROM Cartelera WHERE LOWER(titulo) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titulo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // EXISTE POR TITULO
    public boolean existePorTitulo(String titulo) throws Exception {
        String sql = "SELECT 1 FROM Cartelera WHERE LOWER(titulo) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titulo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // AGREGAR PELICULA (valida duplicados por titulo antes de insertar)
    public Pelicula agregarPelicula(Pelicula p) throws Exception {
        // validaciones a nivel aplicación (además del UNIQUE en BD)
        if (p == null) throw new IllegalArgumentException("Pelicula nula");
        // ejemplo de validaciones mínimas (puedes ampliar)
        if (p.getTitulo() == null || p.getTitulo().trim().isEmpty()) throw new IllegalArgumentException("Titulo requerido");
        if (p.getDirector() == null || p.getDirector().trim().isEmpty()) throw new IllegalArgumentException("Director requerido");

        // prevenir duplicados por titulo
        if (existePorTitulo(p.getTitulo())) {
            throw new IllegalStateException("Ya existe una película con el mismo título.");
        }

        String sql = "INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAno());
            ps.setInt(4, p.getDuracion());
            ps.setString(5, p.getGenero());

            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("No se insertó la película.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                    return p;
                } else {
                    throw new SQLException("No se obtuvo id generado.");
                }
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            // Si por simultaneidad alguien insertó, detectamos constraint
            throw new IllegalStateException("Duplicado detectado por la base de datos (constraint).", ex);
        }
    }

    // ACTUALIZAR PELICULA
    public boolean actualizarPelicula(Pelicula p) throws Exception {
        if (p == null || p.getId() == null) throw new IllegalArgumentException("ID requerido para actualizar");
        String sql = "UPDATE Cartelera SET titulo=?, director=?, ano=?, duracion=?, genero=? WHERE id=?";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAno());
            ps.setInt(4, p.getDuracion());
            ps.setString(5, p.getGenero());
            ps.setInt(6, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR PELICULA
    public boolean eliminarPelicula(int id) throws Exception {
        String sql = "DELETE FROM Cartelera WHERE id = ?";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // INSERTAR VARIAS PELICULAS EN UNA TRANSACCION (ejemplo)
    public void insertarMultipleTransactional(List<Pelicula> peliculas) throws Exception {
        if (peliculas == null || peliculas.isEmpty()) return;
        String sql = "INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);
            try {
                for (Pelicula p : peliculas) {
                    // validación: si ya existe, lanzar excepción para rollback
                    if (existePorTitulo(p.getTitulo())) {
                        throw new IllegalStateException("Existe pelicula con titulo: " + p.getTitulo());
                    }
                    ps.setString(1, p.getTitulo());
                    ps.setString(2, p.getDirector());
                    ps.setInt(3, p.getAno());
                    ps.setInt(4, p.getDuracion());
                    ps.setString(5, p.getGenero());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // row mapper
    private Pelicula mapRow(ResultSet rs) throws SQLException {
        return new Pelicula(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("director"),
                rs.getInt("ano"),
                rs.getInt("duracion"),
                rs.getString("genero")
        );
    }

    public boolean insertar(Pelicula p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object listar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}