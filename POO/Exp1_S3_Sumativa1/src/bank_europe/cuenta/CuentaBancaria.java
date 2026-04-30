/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bank_europe.cuenta;

/**
 *
 * @author Felip
 */
public abstract class CuentaBancaria {
    private String numeroCuenta;
    private double saldo;

    public CuentaBancaria(String numeroCuenta, double saldoInicial) {
        if (numeroCuenta == null || !numeroCuenta.matches("\\d{9}")) {
            throw new IllegalArgumentException("Número de cuenta inválido (9 dígitos)");
        }
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldoInicial;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public double getSaldo() { return saldo; }

    public void depositar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto debe ser mayor a cero");
        saldo += monto;
    }

    public void retirar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto debe ser mayor a cero");
        if (monto > saldo) throw new IllegalArgumentException("Saldo insuficiente");
        saldo -= monto;
    }

    // Método abstracto para cálculo de interés
    public abstract double calcularInteres();
}