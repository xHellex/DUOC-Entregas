/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package threads;

import java.util.Random;
import model.PrimesList;

/**
 *
 * @author Felip
 */
/**
 * Runnable que genera candidatos aleatorios, verifica y añade primos.
 */
public class PrimesThread implements Runnable {
    private final PrimesList primesList;
    private final Random rnd = new Random();

    public PrimesThread(PrimesList list) {
        this.primesList = list;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int candidate = rnd.nextInt(10_000) + 2;
            synchronized (primesList) {
                if (primesList.isPrime(candidate)) {
                    primesList.add(candidate);
                    primesList.notifyAll();
                } else {
                    try {
                        primesList.wait(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}
