/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bank_europe.cuenta.operaciones;

import bank_europe.cuenta.CuentaBancaria;

/**
 *
 * @author Felip
 */
public class CuentaCorriente extends CuentaBancaria {
    private final double comisionPorRetiro = 1000; // monto fijo

    public CuentaCorriente(String numeroCuenta, double saldoInicial) {
        super(numeroCuenta, saldoInicial);
    }

    @Override
    public void retirar(double monto) {
        super.retirar(monto + comisionPorRetiro);
    }

    @Override
    public double calcularInteres() {
        // Cuenta corriente no genera interés
        return 0;
    }
}