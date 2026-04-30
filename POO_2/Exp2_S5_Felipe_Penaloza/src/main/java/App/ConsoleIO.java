/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

/**
 *
 * @author Felip
 */
/**
 * Abstracción de entrada/salida de consola, para facilitar pruebas.
 */
public interface ConsoleIO {
    String readLine(String prompt);
    void println(String text);
}