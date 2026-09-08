package com.bancoxyz.bff.cajero.controller;

import com.bancoxyz.bff.cajero.dto.RetiroRequest;
import com.bancoxyz.bff.cajero.dto.RetiroResponse;
import com.bancoxyz.bff.cajero.dto.SaldoCajeroDTO;
import com.bancoxyz.bff.core.model.Cuenta;
import com.bancoxyz.bff.core.service.BancoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BFF CAJERO (ATM). Expone una interfaz SEGURA y EFICIENTE enfocada en las
 * OPERACIONES CRITICAS de un cajero automatico: consulta de saldo y retiro.
 * Entrega solo la informacion imprescindible (sin datos personales) y valida
 * estrictamente las operaciones.
 *
 * Ruta base: /api/cajero
 */
@RestController
@RequestMapping("/api/cajero")
public class CajeroBffController {

    private final BancoService banco;

    public CajeroBffController(BancoService banco) {
        this.banco = banco;
    }

    /** Consulta de saldo: respuesta minima y sin datos personales. */
    @GetMapping("/cuentas/{id}/saldo")
    public ResponseEntity<SaldoCajeroDTO> consultarSaldo(@PathVariable Long id) {
        return banco.obtenerCuenta(id)
                .map(c -> ResponseEntity.ok(SaldoCajeroDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Operacion critica de retiro, con validacion de monto y saldo. */
    @PostMapping("/cuentas/{id}/retiro")
    public ResponseEntity<RetiroResponse> retirar(@PathVariable Long id,
                                                  @Valid @RequestBody RetiroRequest request) {
        try {
            Cuenta cuenta = banco.retirar(id, request.monto());
            return ResponseEntity.ok(new RetiroResponse(
                    true, "Retiro exitoso", cuenta.getSaldo()));
        } catch (IllegalStateException e) {
            // saldo insuficiente
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RetiroResponse(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            // cuenta no encontrada o monto invalido
            return ResponseEntity.badRequest()
                    .body(new RetiroResponse(false, e.getMessage(), null));
        }
    }
}
