/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bank_europe.cuenta.intereses;

import bank_europe.cuenta.CuentaBancaria;

/**
 *
 * @author Felip
 */
public class CuentaAhorro extends CuentaBancaria {
    private final double tasaInteres = 0.015;

    public CuentaAhorro(String numeroCuenta, double saldoInicial) {
        super(numeroCuenta, saldoInicial);
    }

    @Override
    public double calcularInteres() {
        return getSaldo() * tasaInteres;
    }
}