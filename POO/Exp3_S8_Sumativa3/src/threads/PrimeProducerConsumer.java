/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import model.PrimesList;

/**
 *
 * @author Felip
 */
/**
 * Demo de Queue como buffer entre productor y consumidor.
 */
public class PrimeProducerConsumer {
    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

    public void start() throws InterruptedException {
        Thread producer = new Thread(() -> {
            for (int i = 2; i <= 1000; i++) {
                if (new PrimesList().isPrime(i)) {
                    try { queue.put(i); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Integer p;
                while ((p = queue.take()) != null) {
                    System.out.printf("[Consumer] Procesado primo: %d%n", p);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start(); consumer.start();
        producer.join();
        consumer.interrupt();
    }
}