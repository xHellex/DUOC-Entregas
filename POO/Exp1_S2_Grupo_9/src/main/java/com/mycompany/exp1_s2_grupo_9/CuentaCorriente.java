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
 * Subclase CuentaCorriente.
 */
public class CuentaCorriente extends Cuenta {
    public CuentaCorriente(String numero, double saldoInicial) {
        super(numero, saldoInicial);
    }
    @Override
    public String obtenerTipoCuenta() { return "Cuenta Corriente"; }
}

