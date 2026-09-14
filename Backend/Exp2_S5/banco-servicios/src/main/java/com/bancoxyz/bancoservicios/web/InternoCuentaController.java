package com.bancoxyz.bancoservicios.web;

import com.bancoxyz.bancoservicios.model.Cuenta;
import com.bancoxyz.bancoservicios.model.Tarjeta;
import com.bancoxyz.bancoservicios.model.Transaccion;
import com.bancoxyz.bancoservicios.service.CuentaService;
import com.bancoxyz.bancoservicios.service.TarjetaService;
import com.bancoxyz.bancoservicios.service.TransaccionService;
import com.bancoxyz.bancoservicios.web.dto.ErrorResponse;
import com.bancoxyz.bancoservicios.web.dto.RetiroInternoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API interna de banco-servicios: la unica puerta de entrada a los datos de
 * Cuentas, Transacciones y Tarjetas. La consumen por HTTP los tres BFF (web,
 * movil, cajero), cada uno como proceso independiente en otro puerto. Este
 * servicio no conoce a ningun BFF ni su forma de presentar los datos.
 *
 * Ruta base: /api/interno
 */
@RestController
@RequestMapping("/api/interno")
public class InternoCuentaController {

    private final CuentaService cuentaService;
    private final TransaccionService transaccionService;
    private final TarjetaService tarjetaService;

    public InternoCuentaController(CuentaService cuentaService,
                                   TransaccionService transaccionService,
                                   TarjetaService tarjetaService) {
        this.cuentaService = cuentaService;
        this.transaccionService = transaccionService;
        this.tarjetaService = tarjetaService;
    }

    @GetMapping("/cuentas")
    public List<Cuenta> listarCuentas() {
        return cuentaService.listar();
    }

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<Cuenta> obtenerCuenta(@PathVariable Long id) {
        return cuentaService.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuentas/{id}/transacciones")
    public List<Transaccion> transacciones(@PathVariable Long id) {
        return transaccionService.deCuenta(id);
    }

    @GetMapping("/cuentas/{id}/tarjetas")
    public List<Tarjeta> tarjetas(@PathVariable Long id) {
        return tarjetaService.deCuenta(id);
    }

    @PostMapping("/cuentas/{id}/retiro")
    public ResponseEntity<?> retirar(@PathVariable Long id, @RequestBody RetiroInternoRequest request) {
        try {
            Cuenta cuenta = cuentaService.retirar(id, request.monto());
            return ResponseEntity.ok(cuenta);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
