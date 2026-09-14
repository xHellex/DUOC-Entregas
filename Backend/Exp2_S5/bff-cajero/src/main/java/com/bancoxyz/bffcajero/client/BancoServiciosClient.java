package com.bancoxyz.bffcajero.client;

import com.bancoxyz.bffcajero.client.dto.CuentaDTO;
import com.bancoxyz.bffcajero.client.dto.ErrorResponse;
import com.bancoxyz.bffcajero.client.dto.RetiroInternoRequest;
import com.bancoxyz.bffcajero.client.dto.TarjetaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

/**
 * Unico punto de acceso de este BFF al servicio backend real
 * (banco-servicios), consumido siempre por HTTP. Los errores de negocio del
 * retiro (saldo insuficiente / monto invalido) llegan como
 * HttpClientErrorException.Conflict / .BadRequest y se traducen aqui mismo
 * a las excepciones que el controlador ya sabe manejar.
 */
@Component
public class BancoServiciosClient {

    private final RestClient restClient;

    public BancoServiciosClient(RestClient.Builder builder,
                                @Value("${banco.servicios.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<CuentaDTO> obtenerCuenta(Long id) {
        try {
            return Optional.ofNullable(restClient.get().uri("/api/interno/cuentas/{id}", id)
                    .retrieve().body(CuentaDTO.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<TarjetaDTO> tarjetasDeCuenta(Long id) {
        return restClient.get().uri("/api/interno/cuentas/{id}/tarjetas", id)
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }

    /**
     * @throws IllegalStateException si banco-servicios responde 409 (saldo insuficiente)
     * @throws IllegalArgumentException si banco-servicios responde 400 (monto invalido / cuenta inexistente)
     */
    public CuentaDTO retirar(Long id, double monto) {
        try {
            return restClient.post().uri("/api/interno/cuentas/{id}/retiro", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RetiroInternoRequest(monto))
                    .retrieve().body(CuentaDTO.class);
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException(mensajeDe(e));
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(mensajeDe(e));
        }
    }

    private String mensajeDe(HttpClientErrorException e) {
        try {
            return e.getResponseBodyAs(ErrorResponse.class).mensaje();
        } catch (Exception ex) {
            return e.getStatusText();
        }
    }
}
