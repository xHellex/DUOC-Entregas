/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import Conexion.DatabaseConnection;

import java.lang.reflect.Method;

public class PruebaProps {
    public static void main(String[] args) {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            // imprimir URL del resource (puede ser null)
            System.out.println(">>> Intentando localizar recurso '/db.properties' vía ClassLoader:");
            System.out.println(DatabaseConnection.class.getResource("/db.properties"));

            // reflection para obtener propiedad (solo pruebas)
            Method m = db.getClass().getDeclaredMethod("getProp", String.class);
            m.setAccessible(true);
            Object url = m.invoke(db, "jdbc.url");
            System.out.println("jdbc.url = " + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
