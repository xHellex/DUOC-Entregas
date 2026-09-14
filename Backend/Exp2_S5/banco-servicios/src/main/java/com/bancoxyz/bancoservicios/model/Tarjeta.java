package com.bancoxyz.bancoservicios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjeta")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cuentaId;
    private String tipo;          // credito, debito
    private String marca;         // Visa, Mastercard
    private String numeroTarjeta;
    private Double cupoDisponible;
    private boolean activa;

    public Tarjeta() { }

    public Tarjeta(Long cuentaId, String tipo, String marca, String numeroTarjeta,
                   Double cupoDisponible, boolean activa) {
        this.cuentaId = cuentaId; this.tipo = tipo; this.marca = marca;
        this.numeroTarjeta = numeroTarjeta; this.cupoDisponible = cupoDisponible;
        this.activa = activa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public Double getCupoDisponible() { return cupoDisponible; }
    public void setCupoDisponible(Double cupoDisponible) { this.cupoDisponible = cupoDisponible; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
