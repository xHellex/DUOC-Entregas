/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;

public final class Desktop extends Equipo {
    private int potenciaFuente;
    private String factorForma;

    public Desktop(String modelo, String cpu, int discoMb, int ramGb, BigDecimal precio,
                   int potenciaFuente, String factorForma) {
        super(modelo, cpu, discoMb, ramGb, precio, Tipo.DESKTOP);
        setPotenciaFuente(potenciaFuente);
        setFactorForma(factorForma);
    }

    public int getPotenciaFuente() { return potenciaFuente; }
    public void setPotenciaFuente(int potenciaFuente) {
        if (potenciaFuente <= 0) throw new IllegalArgumentException("Potencia inválida");
        this.potenciaFuente = potenciaFuente;
    }

    public String getFactorForma() { return factorForma; }
    public void setFactorForma(String factorForma) {
        if (factorForma == null || factorForma.trim().isEmpty()) throw new IllegalArgumentException("Factor de forma requerido");
        this.factorForma = factorForma.trim();
    }
}
