# Banco XYZ — Microservicios y resiliencia en la nube con Spring Cloud

**Desarrollo Backend III (PBY2203) — Experiencia 3, Semana 8 (Sumativa individual)**
Felipe Peñaloza Oyarzún

---

## 1. Objetivo del proyecto

Este proyecto es la culminación del ecosistema de microservicios del **Banco XYZ**. Sobre la base de las semanas anteriores (Spring Cloud, Config Server, Eureka, API Gateway, Resilience4j y mensajería con Kafka) se prepara el sistema para un **entorno Cloud resiliente y seguro**, incorporando:

1. **Seguridad con OAuth 2.0** mediante un *Authorization Server* (Spring Authorization Server) que emite y firma los *access tokens*, y microservicios configurados como *Resource Servers* que los validan.
2. **Conteinerización con Docker**: una imagen por cada microservicio (build multi-stage).
3. **Orquestación con Docker Compose**: todo el ecosistema se levanta con un solo comando.
4. **Tolerancia a fallos con Resilience4j** (Circuit Breaker + fallback) en los microservicios.
5. **Mensajería asíncrona con Apache Kafka** (arquitectura orientada a eventos, pub/sub).

Datos base del caso: <https://github.com/KariVillagran/bank_legacy_data>

---

## 2. Arquitectura y estructura del código

```
Exp3_S8_Felipe_Penaloza/
├── docker-compose.yml        # Orquesta TODO el ecosistema
├── config-repo/              # Configuración centralizada servida por el Config Server
│   ├── ms-cuentas.yml  / ms-cuentas-docker.yml
│   ├── ms-transacciones.yml  / ms-transacciones-docker.yml
│   └── ms-tarjetas.yml  / ms-tarjetas-docker.yml
├── config-server/            # (8888) Configuración centralizada (perfil native)
├── eureka-server/            # (8761) Registro y descubrimiento de servicios
├── auth-server/              # (9000) Authorization Server OAuth2.0  ← NUEVO en S8
├── api-gateway/              # (8080) Puerta de entrada única
├── ms-cuentas/               # (8081) Resource Server + consumidor Kafka
├── ms-transacciones/         # (8082) Resource Server + productor Kafka
└── ms-tarjetas/              # (8083) Resource Server + consumidor Kafka
```

### Flujo de seguridad (OAuth 2.0)

```
          (1) POST /oauth2/token
          client_credentials                 (2) access_token (JWT)
 Cliente  ─────────────────────▶  auth-server ─────────────────────▶ Cliente
 (BFF /                           (9000)        firma con clave RSA
 cajero /                            │          publica JWK en /oauth2/jwks
 servicio)                          │
          (3) GET /cuentas          │ (4) el Resource Server valida la firma
          Authorization: Bearer ... │     del token contra el JWK Set
          ─────────────────────▶ api-gateway ─────▶ ms-cuentas (Resource Server)
                                   (8080)                (8081)
```

El `auth-server` centraliza la autorización (**delegación de seguridad**): los microservicios ya **no** gestionan credenciales ni emiten tokens (se eliminó el JWT propio de la S7); solo **validan** los tokens emitidos por el servidor de autorización.

### Arquitectura de eventos (Kafka)

`ms-transacciones` **publica** un evento `TransaccionCreadaEvent` en el tópico `transacciones`; `ms-cuentas` y `ms-tarjetas` lo **consumen** de forma independiente (distinto `groupId`) y reaccionan de forma asíncrona y desacoplada.

---

## 3. Tecnologías

- Java 17, Spring Boot 3.4.1, Spring Cloud 2024.0.0
- Spring Authorization Server · Spring Security OAuth2 Resource Server
- Spring Cloud Config · Netflix Eureka · Spring Cloud Gateway
- Resilience4j (Circuit Breaker)
- Apache Kafka (modo KRaft) · Kafka-UI
- H2 (en memoria) · Docker · Docker Compose

---

## 4. Cómo ejecutar

### Opción A — Todo con Docker Compose (recomendada)

Requisitos: Docker y Docker Compose. Recursos sugeridos: ≥ 6 GB de RAM para el motor de Docker (son 8 servicios Java + Kafka).

```bash
# Desde la carpeta Exp3_S8_Felipe_Penaloza/
docker compose up --build
```

El primer arranque compila cada microservicio dentro de su imagen (puede tardar varios minutos). El orden de arranque está controlado por `depends_on` + *healthchecks* y por el reintento del cliente de Config Server.

Puntos de acceso una vez arriba:

| Servicio        | URL                                   |
|-----------------|---------------------------------------|
| Eureka          | http://localhost:8761                 |
| API Gateway     | http://localhost:8080                 |
| Auth Server     | http://localhost:9000                 |
| Kafka-UI        | http://localhost:8090                 |
| ms-cuentas      | http://localhost:8081                 |
| ms-transacciones| http://localhost:8082                 |
| ms-tarjetas     | http://localhost:8083                 |

Para detener: `docker compose down`

### Opción B — Local (desarrollo, sin Docker)

Arrancar en este orden, cada uno en su carpeta con `mvn spring-boot:run`:
`config-server` → `eureka-server` → `auth-server` → `ms-*` → `api-gateway`.
(Para Kafka en local, levantar solo ese contenedor: `docker compose up kafka kafka-ui`; el broker queda en `localhost:9094`.)

---

## 5. Cómo probar OAuth 2.0 (evidencia)

### 5.1 Obtener un access token (flujo client_credentials)

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u "bancoxyz-client:bancoxyz-secret" \
  -d "grant_type=client_credentials" \
  -d "scope=cuentas.read"
```

Respuesta (JWT):

```json
{ "access_token": "eyJraWQiOi...", "token_type": "Bearer", "expires_in": 3599, "scope": "cuentas.read" }
```

### 5.2 Llamar un servicio protegido CON token (200 OK)

```bash
TOKEN="<pega-aquí-el-access_token>"
curl http://localhost:8080/cuentas -H "Authorization: Bearer $TOKEN"
```

### 5.3 Llamar SIN token (401 Unauthorized) — demuestra la protección

```bash
curl -i http://localhost:8080/cuentas
# HTTP/1.1 401 Unauthorized
```

> Credenciales del cliente (académicas): `bancoxyz-client` / `bancoxyz-secret` (almacenado cifrado con BCrypt).

---

## 6. Cómo probar la resiliencia (Resilience4j)

Los endpoints de negocio están anotados con `@CircuitBreaker` y un método *fallback*. Si la capa de datos falla, el sistema responde con la respuesta de respaldo en vez de propagar el error. El estado del *circuit breaker* se puede observar en:

```
http://localhost:8081/actuator/circuitbreakers
```

## 7. Cómo probar la mensajería (Kafka)

Registrar una transacción (publica un evento):

```bash
curl -X POST http://localhost:8080/transacciones \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"cuentaId":1,"monto":5000,"tipo":"credito"}'
```

- En los logs de `ms-cuentas` se ve `[KAFKA-CONSUMER]` actualizando el saldo.
- En los logs de `ms-tarjetas` se ve el registro del movimiento.
- En **Kafka-UI** (http://localhost:8090) se puede inspeccionar el tópico `transacciones`.

---

## 8. Evidencia de ejecución a adjuntar

1. `docker compose up` con todos los contenedores `healthy` / levantados.
2. `docker images` mostrando las imágenes `bancoxyz/*:1.0.0`.
3. Dashboard de Eureka con todos los servicios registrados.
4. Obtención del token (5.1) y llamada protegida 200 (5.2).
5. Llamada sin token 401 (5.3).
6. Estado del Circuit Breaker (sección 6).
7. Kafka-UI con el tópico `transacciones` y logs de consumo.

---

## 9. Mapa de la pauta (sumativa, 100 pts)

| # | Criterio | Pts | Dónde |
|---|----------|-----|-------|
| 1 | OAuth 2.0 funcional | 20 | `auth-server/` + `SecurityConfig` de cada ms |
| 2 | Imágenes Docker de todos los microservicios | 20 | `Dockerfile` en cada módulo |
| 3 | `docker-compose.yaml` orquestando todo | 20 | `docker-compose.yml` |
| 4 | Tolerancia a fallos con Resilience4j | 20 | `@CircuitBreaker` en los controladores |
| 5 | Mensajería asíncrona (Kafka) | 15 | `event/` en los 3 ms |
| 6 | Código + documentación + evidencia | 5 | este README + propuesta técnica + capturas |
