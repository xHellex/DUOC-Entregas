/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Conexion.DatabaseConnection;
import Modelo.Equipo;
import Modelo.Laptop;
import Modelo.Desktop;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public Equipo insertar(Equipo e) throws Exception {
        String sql = "INSERT INTO Equipos (modelo,cpu,disco_mb,ram_gb,precio,tipo) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getModelo());
            ps.setString(2, e.getCpu());
            ps.setInt(3, e.getDiscoMb());
            ps.setInt(4, e.getRamGb());
            ps.setBigDecimal(5, e.getPrecio());
            ps.setString(6, e.getTipo().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }

            if (e instanceof Laptop) {
                Laptop l = (Laptop) e;
                String s = "INSERT INTO Laptop (equipo_id,pantalla_pulgadas,touch,puertos_usb) VALUES (?,?,?,?)";
                try (PreparedStatement ps2 = conn.prepareStatement(s)) {
                    ps2.setInt(1, e.getId());
                    ps2.setDouble(2, l.getPantallaPulgadas());
                    ps2.setBoolean(3, l.isTouch());
                    ps2.setInt(4, l.getPuertosUsb());
                    ps2.executeUpdate();
                }
            } else if (e instanceof Desktop) {
                Desktop d = (Desktop) e;
                String s = "INSERT INTO Desktop (equipo_id,potencia_fuente,factor_forma) VALUES (?,?,?)";
                try (PreparedStatement ps2 = conn.prepareStatement(s)) {
                    ps2.setInt(1, e.getId());
                    ps2.setInt(2, d.getPotenciaFuente());
                    ps2.setString(3, d.getFactorForma());
                    ps2.executeUpdate();
                }
            }
        }
        return e;
    }

    public Equipo buscarPorId(int id) throws Exception {
        String sql = "SELECT id,modelo,cpu,disco_mb,ram_gb,precio,tipo FROM Equipos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String modelo = rs.getString("modelo");
                    String cpu = rs.getString("cpu");
                    int disco = rs.getInt("disco_mb");
                    int ram = rs.getInt("ram_gb");
                    BigDecimal precio = rs.getBigDecimal("precio");
                    String tipo = rs.getString("tipo");
                    if ("LAPTOP".equalsIgnoreCase(tipo)) {
                        // detalles de Laptop pueden leerse si quieres
                        Laptop l = new Laptop(modelo, cpu, disco, ram, precio, 13.3, false, 2);
                        l.setId(id);
                        return l;
                    } else {
                        Desktop d = new Desktop(modelo, cpu, disco, ram, precio, 500, "ATX");
                        d.setId(id);
                        return d;
                    }
                }
                return null;
            }
        }
    }

    public List<Equipo> listarTodos() throws Exception {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT id,modelo,cpu,disco_mb,ram_gb,precio,tipo FROM Equipos ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String modelo = rs.getString("modelo");
                String cpu = rs.getString("cpu");
                int disco = rs.getInt("disco_mb");
                int ram = rs.getInt("ram_gb");
                BigDecimal precio = rs.getBigDecimal("precio");
                String tipo = rs.getString("tipo");
                Equipo e;
                if ("LAPTOP".equalsIgnoreCase(tipo)) e = new Laptop(modelo, cpu, disco, ram, precio, 13.3, false, 2);
                else e = new Desktop(modelo, cpu, disco, ram, precio, 500, "ATX");
                e.setId(id);
                lista.add(e);
            }
        }
        return lista;
    }

    public boolean eliminar(int id) throws Exception {
        String sql = "DELETE FROM Equipos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
