/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import io.FileIO;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.PrimesList;
import threads.PrimeProducerConsumer;
import threads.PrimesThread;

/**
 *
 * @author Felip
 */
public class SystemVoteApp {
    public static void main(String[] args) throws Exception {
        PrimesList shared = new PrimesList();
        // Paso 2: carga inicial (maneja ausencia de archivo)
        FileIO.loadFromCSV("primes.csv", shared);

        // Paso 1 & 4: concurrencia avanzada con Executor
        ExecutorService exec = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            exec.submit(new PrimesThread(shared));
        }
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);

        // Paso 5: demostración de Queue
        new PrimeProducerConsumer().start();

        // Paso 6: salida final
        System.out.printf("Total de primos: %d%n", shared.getPrimesCount());

        // Escritura de un ejemplo
        FileIO.writeEncrypted("encrypted.txt", "VotoSeguro123", shared.getPrimesCount());
    }
}
