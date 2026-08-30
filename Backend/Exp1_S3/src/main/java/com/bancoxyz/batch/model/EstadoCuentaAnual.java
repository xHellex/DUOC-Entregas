package com.bancoxyz.batch.model;

import jakarta.persistence.*;

/**
 * Estado de cuenta anual consolidado por cuenta (Job 3). Compila las
 * operaciones de cada cuenta en un unico registro apto para auditoria:
 * numero de movimientos, total de ingresos, total de egresos y saldo neto.
 * Cumple el requisito de "generar un informe detallado para auditorias".
 */
@Entity
@Table(name = "estado_cuenta_anual")
public class EstadoCuentaAnual {

    @Id
    private Long cuentaId;

    private long cantidadMovimientos;
    private double totalIngresos;
    private double totalEgresos;
    private double saldoNeto;

    public EstadoCuentaAnual() { }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public long getCantidadMovimientos() { return cantidadMovimientos; }
    public void setCantidadMovimientos(long v) { this.cantidadMovimientos = v; }
    public double getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(double v) { this.totalIngresos = v; }
    public double getTotalEgresos() { return totalEgresos; }
    public void setTotalEgresos(double v) { this.totalEgresos = v; }
    public double getSaldoNeto() { return saldoNeto; }
    public void setSaldoNeto(double v) { this.saldoNeto = v; }
}
