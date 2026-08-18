package com.bancoxyz.batch.model;

import jakarta.persistence.*;

/**
 * Entidad de cuenta con el interes mensual ya calculado y el saldo final
 * actualizado. Es la salida del Job 2.
 */
@Entity
@Table(name = "cuenta_interes")
public class CuentaInteres {

    @Id
    private Long cuentaId;

    private String nombre;
    private Double saldoInicial;
    private Integer edad;
    private String tipo;
    private Double tasaAplicada;
    private Double interesGenerado;
    private Double saldoFinal;

    public CuentaInteres() { }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(Double saldoInicial) { this.saldoInicial = saldoInicial; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getTasaAplicada() { return tasaAplicada; }
    public void setTasaAplicada(Double tasaAplicada) { this.tasaAplicada = tasaAplicada; }
    public Double getInteresGenerado() { return interesGenerado; }
    public void setInteresGenerado(Double interesGenerado) { this.interesGenerado = interesGenerado; }
    public Double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(Double saldoFinal) { this.saldoFinal = saldoFinal; }
}
