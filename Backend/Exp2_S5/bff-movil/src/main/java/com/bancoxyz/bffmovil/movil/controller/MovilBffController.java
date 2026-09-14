package com.bancoxyz.bffmovil.movil.controller;

import com.bancoxyz.bffmovil.client.BancoServiciosClient;
import com.bancoxyz.bffmovil.client.dto.CuentaDTO;
import com.bancoxyz.bffmovil.movil.dto.CuentaMovilDTO;
import com.bancoxyz.bffmovil.movil.dto.ResumenMovilDTO;
import com.bancoxyz.bffmovil.movil.dto.TransaccionMovilDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BFF MOVIL. Respuestas ligeras y un endpoint AGREGADO (/resumen) que compone
 * tres llamadas HTTP a banco-servicios en una vista reducida: saldo, numero
 * de tarjetas activas y las ultimas transacciones. Optimizado para ancho de
 * banda movil.
 *
 * Ruta base: /api/movil
 */
@RestController
@RequestMapping("/api/movil")
public class MovilBffController {

    private static final int MAX_TX_MOVIL = 3;

    private final BancoServiciosClient client;

    public MovilBffController(BancoServiciosClient client) {
        this.client = client;
    }

    @GetMapping("/cuentas")
    public List<CuentaMovilDTO> listarCuentas() {
        return client.listarCuentas().stream().map(CuentaMovilDTO::desde).toList();
    }

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaMovilDTO> obtenerCuenta(@PathVariable Long id) {
        return client.obtenerCuenta(id)
                .map(c -> ResponseEntity.ok(CuentaMovilDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint AGREGADO movil: compone tres llamadas HTTP a banco-servicios
     * en un resumen ligero. Cuenta las tarjetas activas y limita las
     * transacciones a las mas recientes.
     */
    @GetMapping("/cuentas/{id}/resumen")
    public ResponseEntity<ResumenMovilDTO> resumen(@PathVariable Long id) {
        return client.obtenerCuenta(id).map((CuentaDTO cuenta) -> {
            long tarjetasActivas = client.tarjetasDeCuenta(id).stream()
                    .filter(t -> t.activa()).count();
            List<TransaccionMovilDTO> ultimas = client.transaccionesDeCuenta(id).stream()
                    .limit(MAX_TX_MOVIL).map(TransaccionMovilDTO::desde).toList();
            ResumenMovilDTO resumen = new ResumenMovilDTO(
                    cuenta.titular(),
                    cuenta.saldo(),
                    enmascarar(cuenta.numeroCuenta()),
                    (int) tarjetasActivas,
                    ultimas);
            return ResponseEntity.ok(resumen);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuentas/{id}/transacciones")
    public List<TransaccionMovilDTO> transaccionesRecientes(@PathVariable Long id) {
        return client.transaccionesDeCuenta(id).stream()
                .limit(MAX_TX_MOVIL).map(TransaccionMovilDTO::desde).toList();
    }

    private String enmascarar(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        return "**** " + numero.substring(numero.length() - 4);
    }
}
