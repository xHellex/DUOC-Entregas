package com.bancoxyz.bff.movil.controller;

import com.bancoxyz.bff.core.model.Cuenta;
import com.bancoxyz.bff.core.service.CuentaService;
import com.bancoxyz.bff.core.service.TarjetaService;
import com.bancoxyz.bff.core.service.TransaccionService;
import com.bancoxyz.bff.movil.dto.CuentaMovilDTO;
import com.bancoxyz.bff.movil.dto.ResumenMovilDTO;
import com.bancoxyz.bff.movil.dto.TransaccionMovilDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BFF MOVIL. Respuestas ligeras y un endpoint AGREGADO (/resumen) que compone
 * los tres servicios backend en una vista reducida: saldo, numero de tarjetas
 * activas y las ultimas transacciones. Optimizado para ancho de banda movil.
 *
 * Ruta base: /api/movil
 */
@RestController
@RequestMapping("/api/movil")
public class MovilBffController {

    private static final int MAX_TX_MOVIL = 3;

    private final CuentaService cuentaService;
    private final TransaccionService transaccionService;
    private final TarjetaService tarjetaService;

    public MovilBffController(CuentaService cuentaService,
                             TransaccionService transaccionService,
                             TarjetaService tarjetaService) {
        this.cuentaService = cuentaService;
        this.transaccionService = transaccionService;
        this.tarjetaService = tarjetaService;
    }

    @GetMapping("/cuentas")
    public List<CuentaMovilDTO> listarCuentas() {
        return cuentaService.listar().stream().map(CuentaMovilDTO::desde).toList();
    }

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaMovilDTO> obtenerCuenta(@PathVariable Long id) {
        return cuentaService.obtener(id)
                .map(c -> ResponseEntity.ok(CuentaMovilDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint AGREGADO movil: compone los tres servicios en un resumen ligero.
     * Cuenta las tarjetas activas y limita las transacciones a las mas recientes.
     */
    @GetMapping("/cuentas/{id}/resumen")
    public ResponseEntity<ResumenMovilDTO> resumen(@PathVariable Long id) {
        return cuentaService.obtener(id).map(cuenta -> {
            long tarjetasActivas = tarjetaService.deCuenta(id).stream()
                    .filter(t -> t.isActiva()).count();
            List<TransaccionMovilDTO> ultimas = transaccionService.deCuenta(id).stream()
                    .limit(MAX_TX_MOVIL).map(TransaccionMovilDTO::desde).toList();
            ResumenMovilDTO resumen = new ResumenMovilDTO(
                    cuenta.getTitular(),
                    cuenta.getSaldo(),
                    enmascarar(cuenta.getNumeroCuenta()),
                    (int) tarjetasActivas,
                    ultimas);
            return ResponseEntity.ok(resumen);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuentas/{id}/transacciones")
    public List<TransaccionMovilDTO> transaccionesRecientes(@PathVariable Long id) {
        return transaccionService.deCuenta(id).stream()
                .limit(MAX_TX_MOVIL).map(TransaccionMovilDTO::desde).toList();
    }

    private String enmascarar(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        return "**** " + numero.substring(numero.length() - 4);
    }
}
