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
public class CuentaDigital extends CuentaBancaria {
    private static final double BONO_DIGITAL = 0.02;

    public CuentaDigital(String numeroCuenta, double saldoInicial) {
        super(numeroCuenta, saldoInicial);
    }

    /**
     *
     * @return
     */
    @Override
    public double calcularInteres() {
        // Cuenta digital ofrece un bono extra
        return getSaldo() * BONO_DIGITAL;
    }
}