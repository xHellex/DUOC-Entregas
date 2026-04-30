/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Felip
 */
/**
 * Lista sincronizada de números primos.
 */
public class PrimesList extends ArrayList<Integer> {
    private static final long serialVersionUID = 1L;

    @Override
    public synchronized boolean add(Integer valor) {
        if (valor == null || !isPrime(valor)) {
            throw new IllegalArgumentException("Sólo se admiten primos: " + valor);
        }
        return super.add(valor);
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (!(o instanceof Integer) || !isPrime((Integer) o)) {
            throw new IllegalArgumentException("Sólo se pueden eliminar primos: " + o);
        }
        return super.remove(o);
    }

    public synchronized int getPrimesCount() {
        return this.size();
    }

    public boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}