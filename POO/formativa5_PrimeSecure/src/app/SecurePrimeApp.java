/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import primos.PrimesList;
import thread.PrimeProducer;

/**
 *
 * @author Felip
 */
public class SecurePrimeApp {
    public static void main(String[] args) {
        PrimesList shared = new PrimesList();

        // Creamos dos hilos que cubren rangos distintos
        PrimeProducer p1 = new PrimeProducer(2,   500, shared);
        PrimeProducer p2 = new PrimeProducer(501, 1000, shared);

        // Arrancamos ambos hilos
        p1.start();
        p2.start();

        // Esperamos a que terminen
        try {
            p1.join();
            p2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Hilo principal interrumpido");
        }

        // Al terminar, mostramos cuántos primos se encontraron
        System.out.printf("Total de códigos primos generados: %d%n", shared.getPrimesCount());
    }
}