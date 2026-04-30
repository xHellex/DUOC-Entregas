/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Venta {
    private Integer id;
    private String clienteRut;
    private Integer equipoId;
    private LocalDateTime fecha;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoAplicado;
    private BigDecimal precioFinal;
    private String vendedor;

    public Venta(String clienteRut, Integer equipoId, BigDecimal precioUnitario, BigDecimal descuentoAplicado, String vendedor) {
        if (clienteRut == null || clienteRut.trim().isEmpty()) throw new IllegalArgumentException("RUT cliente requerido");
        if (equipoId == null) throw new IllegalArgumentException("Equipo requerido");
        this.clienteRut = clienteRut.trim();
        this.equipoId = equipoId;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = (descuentoAplicado == null) ? BigDecimal.ZERO : descuentoAplicado;
        this.precioFinal = this.precioUnitario.subtract(this.descuentoAplicado);
        this.vendedor = (vendedor == null || vendedor.isBlank()) ? "DEFAULT" : vendedor;
        this.fecha = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClienteRut() { return clienteRut; }
    public Integer getEquipoId() { return equipoId; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
    public BigDecimal getPrecioFinal() { return precioFinal; }
    public String getVendedor() { return vendedor; }

    @Override
    public String toString() {
        return "Venta{" + "id=" + id + ", clienteRut='" + clienteRut + '\'' + ", equipoId=" + equipoId + ", fecha=" + fecha + ", precioFinal=" + precioFinal + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venta)) return false;
        Venta venta = (Venta) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
