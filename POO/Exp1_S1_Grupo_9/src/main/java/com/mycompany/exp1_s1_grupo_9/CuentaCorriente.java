/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.exp1_s1_grupo_9;

/**
 *
 * @author Felip
 */
public class CuentaCorriente {
    private String numero;
    private int saldo;

    public CuentaCorriente(String numero) {
        if (numero == null || numero.length() != 9) {
            throw new IllegalArgumentException("Número de cuenta debe tener 9 dígitos");
        }
        this.numero = numero;
        this.saldo = 0;
    }

    public String getNumero() {
        return numero;
    }

    public int getSaldo() {
        return saldo;
    }

    public void depositar(int monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor que cero");
        saldo += monto;
    }

    public void girar(int monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor que cero");
        if (monto > saldo) throw new IllegalArgumentException("Saldo insuficiente");
        saldo -= monto;
    }
}