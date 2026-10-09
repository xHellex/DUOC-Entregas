package com.bancoxyz.bff.cajero.controller;

import com.bancoxyz.bff.cajero.dto.RetiroRequest;
import com.bancoxyz.bff.cajero.dto.RetiroResponse;
import com.bancoxyz.bff.cajero.dto.SaldoCajeroDTO;
import com.bancoxyz.bff.core.model.Cuenta;
import com.bancoxyz.bff.core.service.CuentaService;
import com.bancoxyz.bff.core.service.TarjetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BFF CAJERO (ATM). Interfaz segura y minima para operaciones criticas:
 * consulta de saldo y retiro. Integra los servicios de cuentas y tarjetas
 * (para informar cuantas tarjetas activas tiene la cuenta), manteniendo la
 * respuesta acotada y sin datos personales.
 *
 * Ruta base: /api/cajero
 */
@RestController
@RequestMapping("/api/cajero")
public class CajeroBffController {

    private final CuentaService cuentaService;
    private final TarjetaService tarjetaService;

    public CajeroBffController(CuentaService cuentaService, TarjetaService tarjetaService) {
        this.cuentaService = cuentaService;
        this.tarjetaService = tarjetaService;
    }

    /**
     * Consulta de saldo: respuesta minima. Agrega del servicio de tarjetas el
     * numero de tarjetas activas, dato util en el cajero sin exponer detalles.
     */
    @GetMapping("/cuentas/{id}/saldo")
    public ResponseEntity<SaldoCajeroDTO> consultarSaldo(@PathVariable Long id) {
        return cuentaService.obtener(id).map(c -> {
            long tarjetasActivas = tarjetaService.deCuenta(id).stream()
                    .filter(t -> t.isActiva()).count();
            return ResponseEntity.ok(SaldoCajeroDTO.desde(c, (int) tarjetasActivas));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cuentas/{id}/retiro")
    public ResponseEntity<RetiroResponse> retirar(@PathVariable Long id,
                                                  @Valid @RequestBody RetiroRequest request) {
        try {
            Cuenta cuenta = cuentaService.retirar(id, request.monto());
            return ResponseEntity.ok(new RetiroResponse(
                    true, "Retiro exitoso", cuenta.getSaldo()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RetiroResponse(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new RetiroResponse(false, e.getMessage(), null));
        }
    }
}
