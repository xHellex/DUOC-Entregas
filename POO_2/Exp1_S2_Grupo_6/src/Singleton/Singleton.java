/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Singleton;

/**
 *
 * @author Felipe Peñaloza Oyarzún & Joaquín Gómez Flores
 */
/**
 * Clase de ejemplo que demuestra el patrón singleton con instancia privada, estática y final.
 */
public class Singleton {
    private static final Singleton INSTANCIA = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCIA;
    }
}