package com.bancoxyz.batch.model;

/**
 * DTO crudo leido del CSV de transacciones. Los campos son String porque
 * el archivo legacy contiene valores mal formateados (fechas invalidas,
 * montos vacios) que no pueden mapearse directamente a tipos fuertes.
 * La conversion y validacion ocurre en el ItemProcessor.
 */
public class TransaccionInput {
    private String id;
    private String fecha;
    private String monto;
    private String tipo;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getMonto() { return monto; }
    public void setMonto(String monto) { this.monto = monto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
