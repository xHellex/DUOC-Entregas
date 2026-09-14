package com.bancoxyz.bffweb.web.controller;

import com.bancoxyz.bffweb.client.BancoServiciosClient;
import com.bancoxyz.bffweb.client.dto.CuentaDTO;
import com.bancoxyz.bffweb.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BFF WEB. Optimizado para navegadores: respuestas completas y, sobre todo,
 * un endpoint AGREGADO (/dashboard) que compone la informacion de
 * banco-servicios (cuenta + transacciones + tarjetas, tres llamadas HTTP
 * independientes) en una sola respuesta.
 *
 * Ruta base: /api/web
 */
@RestController
@RequestMapping("/api/web")
public class WebBffController {

    private final BancoServiciosClient client;

    public WebBffController(BancoServiciosClient client) {
        this.client = client;
    }

    @GetMapping("/cuentas")
    public List<CuentaWebDTO> listarCuentas() {
        return client.listarCuentas().stream().map(CuentaWebDTO::desde).toList();
    }

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaWebDTO> obtenerCuenta(@PathVariable Long id) {
        return client.obtenerCuenta(id)
                .map(c -> ResponseEntity.ok(CuentaWebDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint AGREGADO: construye un dashboard completo combinando tres
     * llamadas HTTP a banco-servicios en una sola respuesta para el cliente web.
     */
    @GetMapping("/cuentas/{id}/dashboard")
    public ResponseEntity<DashboardWebDTO> dashboard(@PathVariable Long id) {
        return client.obtenerCuenta(id).map((CuentaDTO cuenta) -> {
            List<TransaccionWebDTO> tx = client.transaccionesDeCuenta(id).stream()
                    .map(TransaccionWebDTO::desde).toList();
            List<TarjetaWebDTO> tarjetas = client.tarjetasDeCuenta(id).stream()
                    .map(TarjetaWebDTO::desde).toList();
            DashboardWebDTO dash = new DashboardWebDTO(
                    CuentaWebDTO.desde(cuenta), tx, tarjetas, tx.size(), tarjetas.size());
            return ResponseEntity.ok(dash);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuentas/{id}/transacciones")
    public List<TransaccionWebDTO> transacciones(@PathVariable Long id) {
        return client.transaccionesDeCuenta(id).stream()
                .map(TransaccionWebDTO::desde).toList();
    }
}
