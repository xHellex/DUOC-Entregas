package com.bancoxyz.batch.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Resumen agregado del procesamiento de transacciones diarias (Job 1).
 * Se persiste una fila por ejecucion, consolidando totales, montos por tipo
 * y el conteo de anomalias detectadas. Cumple el requisito de "generar un
 * resumen" del enunciado.
 */
@Entity
@Table(name = "resumen_transacciones")
public class ResumenTransacciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaEjecucion;
    private long totalLeidas;
    private long totalValidas;
    private long totalAnomalias;
    private long cantidadDebitos;
    private long cantidadCreditos;
    private double montoTotalDebitos;
    private double montoTotalCreditos;

    public ResumenTransacciones() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaEjecucion() { return fechaEjecucion; }
    public void setFechaEjecucion(LocalDateTime f) { this.fechaEjecucion = f; }
    public long getTotalLeidas() { return totalLeidas; }
    public void setTotalLeidas(long v) { this.totalLeidas = v; }
    public long getTotalValidas() { return totalValidas; }
    public void setTotalValidas(long v) { this.totalValidas = v; }
    public long getTotalAnomalias() { return totalAnomalias; }
    public void setTotalAnomalias(long v) { this.totalAnomalias = v; }
    public long getCantidadDebitos() { return cantidadDebitos; }
    public void setCantidadDebitos(long v) { this.cantidadDebitos = v; }
    public long getCantidadCreditos() { return cantidadCreditos; }
    public void setCantidadCreditos(long v) { this.cantidadCreditos = v; }
    public double getMontoTotalDebitos() { return montoTotalDebitos; }
    public void setMontoTotalDebitos(double v) { this.montoTotalDebitos = v; }
    public double getMontoTotalCreditos() { return montoTotalCreditos; }
    public void setMontoTotalCreditos(double v) { this.montoTotalCreditos = v; }
}
