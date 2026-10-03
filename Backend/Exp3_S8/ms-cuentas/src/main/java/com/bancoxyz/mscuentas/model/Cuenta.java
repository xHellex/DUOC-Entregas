package com.bancoxyz.mscuentas.model;

import jakarta.persistence.*;

/**
 * Cuenta bancaria. Resultado de la migracion batch, ahora expuesta como API
 * de microservicio.
 */
@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    private Long id;
    private String titular;
    private String tipo;
    private Double saldo;

    public Cuenta() { }
    public Cuenta(Long id, String titular, String tipo, Double saldo) {
        this.id = id; this.titular = titular; this.tipo = tipo; this.saldo = saldo;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }
}
