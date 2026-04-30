/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;
import Conexion.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Joaquín Gómez Flores
 */
public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println("[Inicio] Probando conexión...");
        try (Connection conn = DatabaseConnection.obtenerConexion()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexión exitosa a Cine_DB.");
            } else {
                System.out.println("No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        System.out.println("[Fin] Test terminado.");
    }
    
}
