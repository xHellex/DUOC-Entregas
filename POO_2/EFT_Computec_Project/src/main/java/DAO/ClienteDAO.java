/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Conexion.DatabaseConnection;
import Modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean agregar(Cliente c) throws Exception {
        String sql = "INSERT INTO Clientes (rut,nombre,direccion,comuna,email,telefono) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getRut());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getComuna());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getTelefono());
            return ps.executeUpdate() > 0;
        }
    }

    public Cliente buscarPorRut(String rut) throws Exception {
        String sql = "SELECT rut,nombre,direccion,comuna,email,telefono FROM Clientes WHERE rut = ?";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getString("rut"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("comuna"),
                        rs.getString("email"),
                        rs.getString("telefono")
                    );
                }
                return null;
            }
        }
    }

    public List<Cliente> listarTodos() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT rut,nombre,direccion,comuna,email,telefono FROM Clientes ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getString("rut"),
                    rs.getString("nombre"),
                    rs.getString("direccion"),
                    rs.getString("comuna"),
                    rs.getString("email"),
                    rs.getString("telefono")
                ));
            }
        }
        return lista;
    }

    public boolean eliminar(String rut) throws Exception {
        String sql = "DELETE FROM Clientes WHERE rut = ?";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rut);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Cliente c) throws Exception {
        String sql = "UPDATE Clientes SET nombre=?, direccion=?, comuna=?, email=?, telefono=? WHERE rut=?";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getComuna());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getTelefono());
            ps.setString(6, c.getRut());
            return ps.executeUpdate() > 0;
        }
    }
}
