/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package primos;

import java.util.ArrayList;

/**
 * PrimesList es una lista de enteros que sólo admite números primos.
 * Además, sus métodos de add/remove están sincronizados para permitir
 * concurrencia de múltiples hilos.
 */
public class PrimesList extends ArrayList<Integer> {
    private static final long serialVersionUID = 1L;

    /**
     * Comprueba si n es un número primo.
     */
    public boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /**
     * Sólo añade si valor es primo; si no, lanza IllegalArgumentException.
     * Método sincronizado para seguridad en multihilo.
     */
    @Override
    public synchronized boolean add(Integer valor) {
        if (valor == null || !isPrime(valor)) {
            throw new IllegalArgumentException("Sólo se admiten primos: " + valor);
        }
        return super.add(valor);
    }

    /**
     * Sólo elimina si o es un Integer primo; si no, lanza IllegalArgumentException.
     * Método sincronizado para seguridad en multihilo.
     */
    @Override
    public synchronized boolean remove(Object o) {
        if (!(o instanceof Integer) || !isPrime((Integer) o)) {
            throw new IllegalArgumentException("Sólo se pueden eliminar primos: " + o);
        }
        return super.remove(o);
    }

    /**
     * Devuelve cuántos primos hay en la lista.
     * Método sincronizado para reflejar un conteo consistente.
     */
    public synchronized int getPrimesCount() {
        return this.size();
    }
}
