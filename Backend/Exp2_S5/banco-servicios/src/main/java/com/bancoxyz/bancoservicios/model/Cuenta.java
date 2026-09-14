package com.bancoxyz.bancoservicios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    private Long id;

    private String titular;
    private String tipo;          // ahorro, prestamo, hipoteca
    private Double saldo;
    private String numeroCuenta;

    public Cuenta() { }

    public Cuenta(Long id, String titular, String tipo, Double saldo, String numeroCuenta) {
        this.id = id; this.titular = titular; this.tipo = tipo;
        this.saldo = saldo; this.numeroCuenta = numeroCuenta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
}
