/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 *
 * author Joaquín Gómez Flores & Felipe Peñaloza
 * * Lector de propiedades para obtener una conexión JDBC.
 * Busca db.properties en resources y permite override por variables de entorno.
 */
public final class DatabaseConnection {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
            // driver opcionalmente cargado
            String driver = getProp("jdbc.driver");
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error cargando db.properties: " + e.getMessage());
        }
    }

    private DatabaseConnection() {}

    private static String getProp(String key) {
        // primero variables de entorno (override), luego properties file
        String env = System.getenv(key.replace('.', '_').toUpperCase()); // e.g. JDBC_URL
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key);
    }

    public static Connection obtenerConexion() throws Exception {
        String url = getProp("jdbc.url");
        String user = getProp("jdbc.user");
        String pass = getProp("jdbc.password");
        return DriverManager.getConnection(url, user, pass);
    }
}