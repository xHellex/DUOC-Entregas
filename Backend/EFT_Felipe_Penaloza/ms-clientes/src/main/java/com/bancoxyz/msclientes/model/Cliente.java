package com.bancoxyz.msclientes.model;

import jakarta.persistence.*;

/**
 * Cliente del Banco XYZ. El microservicio de Gestion de Clientes administra
 * la informacion personal y el perfil de cada cliente.
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    private Long id;
    private String rut;
    private String nombre;
    private String email;
    private String segmento;   // perfil comercial: estandar, preferente, empresa
    private Long cuentaId;     // cuenta principal asociada

    public Cliente() { }
    public Cliente(Long id, String rut, String nombre, String email, String segmento, Long cuentaId) {
        this.id = id; this.rut = rut; this.nombre = nombre;
        this.email = email; this.segmento = segmento; this.cuentaId = cuentaId;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
}
