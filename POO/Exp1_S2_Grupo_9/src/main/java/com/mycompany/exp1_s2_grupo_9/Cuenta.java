/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.exp1_s2_grupo_9;

/**
 *
 * @author Felip
 */
/**
 * Clase abstracta Cuenta: base para todos los tipos de cuenta.
 */
public abstract class Cuenta {
    protected String numero;
    protected double saldo;

    public Cuenta() {
        this.numero = "000000000";
        this.saldo = 0;
    }

    public Cuenta(String numero, double saldoInicial) {
        if (numero == null || !numero.matches("\\d{9}")) {
            throw new IllegalArgumentException("Número de cuenta inválido: debe tener 9 dígitos");
        }
        this.numero = numero;
        this.saldo = saldoInicial;
    }

    public String getNumero() { return numero; }
    public double getSaldo() { return saldo; }

    public void depositar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor que cero");
        saldo += monto;
    }

    public void retirar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor que cero");
        if (monto > saldo) throw new IllegalArgumentException("Saldo insuficiente");
        saldo -= monto;
    }

    public abstract String obtenerTipoCuenta();
}