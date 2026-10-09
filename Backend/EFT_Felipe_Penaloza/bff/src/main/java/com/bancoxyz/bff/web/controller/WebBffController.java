package com.bancoxyz.bff.web.controller;

import com.bancoxyz.bff.core.service.CuentaService;
import com.bancoxyz.bff.core.service.TarjetaService;
import com.bancoxyz.bff.core.service.TransaccionService;
import com.bancoxyz.bff.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BFF WEB. Optimizado para navegadores: respuestas completas y, sobre todo,
 * un endpoint AGREGADO (/dashboard) que compone la informacion de los tres
 * servicios backend (cuentas, transacciones y tarjetas) en una sola llamada.
 *
 * Ruta base: /api/web
 */
@RestController
@RequestMapping("/api/web")
public class WebBffController {

    private final CuentaService cuentaService;
    private final TransaccionService transaccionService;
    private final TarjetaService tarjetaService;

    public WebBffController(CuentaService cuentaService,
                           TransaccionService transaccionService,
                           TarjetaService tarjetaService) {
        this.cuentaService = cuentaService;
        this.transaccionService = transaccionService;
        this.tarjetaService = tarjetaService;
    }

    @GetMapping("/cuentas")
    public List<CuentaWebDTO> listarCuentas() {
        return cuentaService.listar().stream().map(CuentaWebDTO::desde).toList();
    }

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaWebDTO> obtenerCuenta(@PathVariable Long id) {
        return cuentaService.obtener(id)
                .map(c -> ResponseEntity.ok(CuentaWebDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint AGREGADO: construye un dashboard completo combinando los tres
     * servicios backend en una sola respuesta.
     */
    @GetMapping("/cuentas/{id}/dashboard")
    public ResponseEntity<DashboardWebDTO> dashboard(@PathVariable Long id) {
        return cuentaService.obtener(id).map(cuenta -> {
            List<TransaccionWebDTO> tx = transaccionService.deCuenta(id).stream()
                    .map(TransaccionWebDTO::desde).toList();
            List<TarjetaWebDTO> tarjetas = tarjetaService.deCuenta(id).stream()
                    .map(TarjetaWebDTO::desde).toList();
            DashboardWebDTO dash = new DashboardWebDTO(
                    CuentaWebDTO.desde(cuenta), tx, tarjetas, tx.size(), tarjetas.size());
            return ResponseEntity.ok(dash);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuentas/{id}/transacciones")
    public List<TransaccionWebDTO> transacciones(@PathVariable Long id) {
        return transaccionService.deCuenta(id).stream()
                .map(TransaccionWebDTO::desde).toList();
    }
}
