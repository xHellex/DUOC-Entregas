package com.bancoxyz.bffmovil.client;

import com.bancoxyz.bffmovil.client.dto.CuentaDTO;
import com.bancoxyz.bffmovil.client.dto.TarjetaDTO;
import com.bancoxyz.bffmovil.client.dto.TransaccionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

/**
 * Unico punto de acceso de este BFF al servicio backend real
 * (banco-servicios), consumido siempre por HTTP.
 */
@Component
public class BancoServiciosClient {

    private final RestClient restClient;

    public BancoServiciosClient(RestClient.Builder builder,
                                @Value("${banco.servicios.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<CuentaDTO> listarCuentas() {
        return restClient.get().uri("/api/interno/cuentas")
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public Optional<CuentaDTO> obtenerCuenta(Long id) {
        try {
            return Optional.ofNullable(restClient.get().uri("/api/interno/cuentas/{id}", id)
                    .retrieve().body(CuentaDTO.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<TransaccionDTO> transaccionesDeCuenta(Long id) {
        return restClient.get().uri("/api/interno/cuentas/{id}/transacciones", id)
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public List<TarjetaDTO> tarjetasDeCuenta(Long id) {
        return restClient.get().uri("/api/interno/cuentas/{id}/tarjetas", id)
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }
}
