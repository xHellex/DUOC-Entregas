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
 * Subclase CuentaCredito.
 */
public class CuentaCredito extends Cuenta {
    private double limiteCredito;

    public CuentaCredito(String numero, double limiteCredito) {
        super(numero, 0);
        if (limiteCredito <= 0) throw new IllegalArgumentException("Límite de crédito inválido");
        this.limiteCredito = limiteCredito;
    }

    @Override
    public String obtenerTipoCuenta() { return "Cuenta de Crédito"; }

    @Override
    public void retirar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor que cero");
        if (monto > saldo + limiteCredito) throw new IllegalArgumentException("Límite de crédito excedido");
        saldo -= monto;
    }

    public double getLimiteCredito() { return limiteCredito; }
}
