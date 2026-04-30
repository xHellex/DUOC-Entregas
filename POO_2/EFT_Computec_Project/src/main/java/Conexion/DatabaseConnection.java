/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DatabaseConnection robusta: carga db.properties desde classpath; si no, intenta rutas de proyecto y CWD.
 * Muestra mensajes diagnósticos para ayudar a encontrar el problema.
 */
public final class DatabaseConnection {
    private static final Properties PROPS = new Properties();
    private static DatabaseConnection instance;

    static {
        // 1) intentar cargar desde classpath
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                PROPS.load(in);
                System.out.println("[DB] db.properties cargado desde classpath (/db.properties).");
            } else {
                System.err.println("[DB] No se encontró '/db.properties' en classpath. Intentando rutas alternativas...");
                // 2) intentar cargar desde src/main/resources (útil al ejecutar desde IDE sin empaquetar)
                try {
                    String p = Paths.get("src", "main", "resources", "db.properties").toAbsolutePath().toString();
                    File f = new File(p);
                    if (f.exists()) {
                        try (InputStream in2 = new FileInputStream(f)) {
                            PROPS.load(in2);
                            System.out.println("[DB] db.properties cargado desde: " + p);
                        }
                    } else {
                        // 3) intentar cargar desde working dir (./db.properties)
                        File f2 = new File("db.properties");
                        if (f2.exists()) {
                            try (InputStream in3 = new FileInputStream(f2)) {
                                PROPS.load(in3);
                                System.out.println("[DB] db.properties cargado desde working dir: " + f2.getAbsolutePath());
                            }
                        } else {
                            throw new IllegalStateException("No se encontró db.properties en classpath, ni en 'src/main/resources/db.properties', ni en './db.properties'.");
                        }
                    }
                } catch (Exception ex) {
                    throw new ExceptionInInitializerError("No se pudo cargar db.properties desde rutas alternativas: " + ex.getMessage());
                }
            }

            // intentar cargar driver si está declarado
            String driver = PROPS.getProperty("jdbc.driver");
            if (driver != null && !driver.isBlank()) {
                try {
                    Class.forName(driver);
                } catch (ClassNotFoundException cnfe) {
                    System.err.println("[DB] Advertencia: driver JDBC especificado no encontrado: " + driver);
                }
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error cargando db.properties: " + e.getMessage());
        }
    }

    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    private String getProp(String key) {
        String envKey = key.replace('.', '_').toUpperCase();
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key);
    }

    public Connection obtenerConexion() throws Exception {
        String url = getProp("jdbc.url");
        String user = getProp("jdbc.user");
        String pass = getProp("jdbc.password");

        if (url == null || url.isBlank()) {
            String msg = "La propiedad 'jdbc.url' es nula o vacía. Revisa src/main/resources/db.properties o la variable de entorno JDBC_URL.";
            System.err.println("[DB] " + msg);
            throw new IllegalStateException(msg);
        }
        if (user == null) user = "";
        if (pass == null) pass = "";

        return DriverManager.getConnection(url, user, pass);
    }
}
