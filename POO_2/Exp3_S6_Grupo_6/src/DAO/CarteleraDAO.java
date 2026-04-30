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
 */
public class CarteleraDAO {
    public boolean insertar(Pelicula p) {
        String sql = "INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAno());
            ps.setInt(4, p.getDuracion());
            ps.setString(5, p.getGenero());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error al insertar película: " + e.getMessage());
            return false;
        }
    }

    public List<Pelicula> listar() {
        String sql = "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera ORDER BY id DESC";
        List<Pelicula> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelicula p = new Pelicula(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("director"),
                        rs.getInt("ano"),
                        rs.getInt("duracion"),
                        rs.getString("genero")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar películas: " + e.getMessage());
        }
        return lista;
    }
    
}
