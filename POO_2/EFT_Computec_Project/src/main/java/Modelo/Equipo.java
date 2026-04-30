/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Equipo {
    protected Integer id;
    protected String modelo;
    protected String cpu;
    protected int discoMb;
    protected int ramGb;
    protected BigDecimal precio;
    protected Tipo tipo;

    public enum Tipo { LAPTOP, DESKTOP }

    protected Equipo(String modelo, String cpu, int discoMb, int ramGb, BigDecimal precio, Tipo tipo) {
        setModelo(modelo);
        setCpu(cpu);
        setDiscoMb(discoMb);
        setRamGb(ramGb);
        setPrecio(precio);
        this.tipo = tipo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) throw new IllegalArgumentException("Modelo requerido");
        this.modelo = modelo.trim();
    }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) {
        if (cpu == null || cpu.trim().isEmpty()) throw new IllegalArgumentException("CPU requerido");
        this.cpu = cpu.trim();
    }

    public int getDiscoMb() { return discoMb; }
    public void setDiscoMb(int discoMb) {
        if (discoMb <= 0) throw new IllegalArgumentException("Disco debe ser positivo");
        this.discoMb = discoMb;
    }

    public int getRamGb() { return ramGb; }
    public void setRamGb(int ramGb) {
        if (ramGb <= 0) throw new IllegalArgumentException("RAM debe ser positiva");
        this.ramGb = ramGb;
    }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() < 0) throw new IllegalArgumentException("Precio inválido");
        this.precio = precio;
    }

    public Tipo getTipo() { return tipo; }

    @Override
    public String toString() {
        return "Equipo{" + "id=" + id + ", modelo='" + modelo + '\'' + ", tipo=" + tipo + ", precio=" + precio + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipo)) return false;
        Equipo equipo = (Equipo) o;
        return Objects.equals(id, equipo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
