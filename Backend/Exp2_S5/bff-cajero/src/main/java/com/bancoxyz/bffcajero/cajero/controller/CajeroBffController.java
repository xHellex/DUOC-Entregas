package com.bancoxyz.bffcajero.cajero.controller;

import com.bancoxyz.bffcajero.cajero.dto.RetiroRequest;
import com.bancoxyz.bffcajero.cajero.dto.RetiroResponse;
import com.bancoxyz.bffcajero.cajero.dto.SaldoCajeroDTO;
import com.bancoxyz.bffcajero.client.BancoServiciosClient;
import com.bancoxyz.bffcajero.client.dto.CuentaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BFF CAJERO (ATM). Interfaz segura y minima para operaciones criticas:
 * consulta de saldo y retiro. Agrega el saldo y el numero de tarjetas
 * activas (llamadas HTTP independientes a banco-servicios), manteniendo la
 * respuesta acotada y sin datos personales.
 *
 * Ruta base: /api/cajero
 */
@RestController
@RequestMapping("/api/cajero")
public class CajeroBffController {

    private final BancoServiciosClient client;

    public CajeroBffController(BancoServiciosClient client) {
        this.client = client;
    }

    @GetMapping("/cuentas/{id}/saldo")
    public ResponseEntity<SaldoCajeroDTO> consultarSaldo(@PathVariable Long id) {
        return client.obtenerCuenta(id).map((CuentaDTO c) -> {
            long tarjetasActivas = client.tarjetasDeCuenta(id).stream()
                    .filter(t -> t.activa()).count();
            return ResponseEntity.ok(SaldoCajeroDTO.desde(c, (int) tarjetasActivas));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cuentas/{id}/retiro")
    public ResponseEntity<RetiroResponse> retirar(@PathVariable Long id,
                                                  @Valid @RequestBody RetiroRequest request) {
        try {
            CuentaDTO cuenta = client.retirar(id, request.monto());
            return ResponseEntity.ok(new RetiroResponse(
                    true, "Retiro exitoso", cuenta.saldo()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RetiroResponse(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new RetiroResponse(false, e.getMessage(), null));
        }
    }
}
