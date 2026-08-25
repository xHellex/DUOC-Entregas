package com.bancoxyz.batch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que representa una transaccion diaria ya validada y normalizada.
 * Solo llegan a persistirse las filas que superaron el ItemProcessor.
 */
@Entity
@Table(name = "transaccion")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idOrigen;
    private LocalDate fecha;
    private Double monto;
    private String tipo;

    public Transaccion() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdOrigen() { return idOrigen; }
    public void setIdOrigen(Long idOrigen) { this.idOrigen = idOrigen; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
