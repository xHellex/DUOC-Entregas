package com.bancoxyz.bancoservicios.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transaccion")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cuentaId;
    private LocalDate fecha;
    private Double monto;
    private String tipo;          // debito, credito
    private String descripcion;

    public Transaccion() { }

    public Transaccion(Long cuentaId, LocalDate fecha, Double monto, String tipo, String descripcion) {
        this.cuentaId = cuentaId; this.fecha = fecha; this.monto = monto;
        this.tipo = tipo; this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
