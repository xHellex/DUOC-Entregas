package com.bancoxyz.mstarjetas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjeta")
public class Tarjeta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cuentaId;
    private String tipo;
    private String marca;
    private Double cupoDisponible;
    private boolean activa;

    public Tarjeta() { }
    public Tarjeta(Long cuentaId, String tipo, String marca, Double cupoDisponible, boolean activa) {
        this.cuentaId = cuentaId; this.tipo = tipo; this.marca = marca;
        this.cupoDisponible = cupoDisponible; this.activa = activa;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Double getCupoDisponible() { return cupoDisponible; }
    public void setCupoDisponible(Double c) { this.cupoDisponible = c; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
