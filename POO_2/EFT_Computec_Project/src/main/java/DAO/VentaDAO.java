/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Conexion.DatabaseConnection;
import Modelo.Venta;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public Venta registrarVenta(Venta v) throws Exception {
        String sql = "INSERT INTO Ventas (cliente_rut,equipo_id,precio_final) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getClienteRut());
            ps.setInt(2, v.getEquipoId());
            ps.setBigDecimal(3, v.getPrecioFinal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) v.setId(rs.getInt(1));
            }
        }
        return v;
    }

    public List<Venta> listarTodos() throws Exception {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id,cliente_rut,equipo_id,fecha,precio_final FROM Ventas ORDER BY fecha DESC";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Venta v = new Venta(
                    rs.getString("cliente_rut"),
                    rs.getInt("equipo_id"),
                    rs.getBigDecimal("precio_final"),
                    BigDecimal.ZERO,
                    "DEFAULT"
                );
                v.setId(rs.getInt("id"));
                lista.add(v);
            }
        }
        return lista;
    }

    public int contarVentas() throws Exception {
        String sql = "SELECT COUNT(*) FROM Ventas";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    public BigDecimal totalRecaudado() throws Exception {
        String sql = "SELECT COALESCE(SUM(precio_final),0) FROM Ventas";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
            return BigDecimal.ZERO;
        }
    }
}
