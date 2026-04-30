/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thread;

import primos.PrimesList;

/**
 *
 * @author Felip
 */
public class PrimeProducer extends Thread {
    private final int from, to;
    private final PrimesList target;

    public PrimeProducer(int from, int to, PrimesList target) {
        this.from = from;
        this.to = to;
        this.target = target;
    }

    @Override
    public void run() {
        for (int n = from; n <= to; n++) {
            if (target.isPrime(n)) {
                try {
                    target.add(n);
                    System.out.printf("[%s] Añadido primo: %d%n", getName(), n);
                } catch (IllegalArgumentException ignored) {
                    // no puede suceder porque isPrime ya lo comprobó
                }
            }
        }
    }
}