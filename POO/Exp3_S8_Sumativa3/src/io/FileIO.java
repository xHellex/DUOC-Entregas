/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.PrimesList;

/**
 *
 * @author Felip
 */
/**
 * Manejo de carga y escritura de datos.
 */
public class FileIO {
    public static void loadFromCSV(String path, PrimesList list) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Aviso: '" + path + "' no encontrado. Se creará vacío.");
            file.createNewFile();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                // Omitir silenciosamente líneas vacías
                if (trimmed.isEmpty()) {
                    continue;
                }
                // Sólo dígitos aceptados
                if (!trimmed.matches("\\d+")) {
                    System.err.println("Línea inválida en CSV: '" + line + "'. Omitida.");
                    continue;
                }
                int p = Integer.parseInt(trimmed);
                list.add(p);
            }
        }
    }

    public static void writeEncrypted(String path, String msg, int code) throws IOException {
        try (FileWriter fw = new FileWriter(path, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Mensaje: " + msg + " | Código primo: " + code);
            bw.newLine();
        }
    }
}
