/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * author Joaquín Gómez Flores & Felipe Peñaloza
 */
public class DatabaseConnection {
    // URL con zona horaria de Chile y sin SSL para entorno local
    private static final String URL = "jdbc:mysql://localhost:3306/Cine_DB"
            + "?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true"
            + "&useUnicode=true&characterEncoding=UTF-8";
    private static final String USUARIO = "Grupo6";
    private static final String CLAVE = "Grupo625";

    /**
     * Retorna una conexión válida o lanza SQLException si falla.
     * @return 
     * @throws java.sql.SQLException
     */
    public static Connection obtenerConexion() throws SQLException {
        // Desde MySQL Connector/J moderno no es necesario Class.forName(...)
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
    
}
