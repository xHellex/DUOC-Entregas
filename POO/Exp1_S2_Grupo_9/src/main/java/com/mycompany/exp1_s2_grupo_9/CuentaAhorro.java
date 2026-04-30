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
 * Subclase CuentaAhorro.
 */
public class CuentaAhorro extends Cuenta {
    private double tasaInteres;

    public CuentaAhorro(String numero) {
        super(numero, 0);
        this.tasaInteres = 0.01;
    }

    public CuentaAhorro(String numero, double saldoInicial, double tasaInteres) {
        super(numero, saldoInicial);
        if (tasaInteres < 0) throw new IllegalArgumentException("Tasa de interés inválida");
        this.tasaInteres = tasaInteres;
    }

    @Override
    public String obtenerTipoCuenta() { return "Cuenta de Ahorro"; }
    public void aplicarInteres() { saldo += saldo * tasaInteres; }
}
