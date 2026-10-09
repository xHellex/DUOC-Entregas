# EFT · Parte 3 — Microservicios Resilientes con Spring Cloud

Ecosistema de microservicios del Banco XYZ: los **3 servicios clave** de la pauta más la infraestructura cloud, con OAuth2, Resilience4j y Kafka.

## Componentes

| Componente | Puerto | Rol |
|-----------|--------|-----|
| config-server | 8888 | Configuración centralizada (perfil native) |
| eureka-server | 8761 | Registro y descubrimiento |
| auth-server | 9000 | Authorization Server OAuth2.0 (emite JWT) |
| api-gateway | 8080 | Puerta de entrada única (enruta por Eureka) |
| **ms-cuentas** | 8081 | **Gestión de Cuentas** (apertura, saldo) · consumidor Kafka |
| **ms-pagos** | 8082 | **Procesamiento de Pagos** (pagos/transferencias) · productor Kafka |
| **ms-clientes** | 8083 | **Gestión de Clientes** (datos personales, perfil) · consumidor Kafka |

## Cómo cubre la pauta

- **Criterio 5 (15 pts) — 3 microservicios resilientes y seguros:** Cuentas, Pagos y Clientes, cada uno como **OAuth2 Resource Server** (valida JWT contra el JWK del auth-server) y con **Resilience4j** (`@CircuitBreaker` + fallback). Integración **Kafka**: ms-pagos publica en el tópico `pagos`; ms-cuentas (actualiza saldo) y ms-clientes (notifica/alerta) consumen con groupId distinto (pub/sub).
- **Criterio 6 (10 pts) — Docker + escalabilidad horizontal:** cada módulo tiene su `Dockerfile` multi-stage; `docker-compose.yml` orquesta todo y los 3 microservicios de negocio están preparados para **réplicas** (ver abajo). El despliegue en AWS está en `despliegue.md` (pendiente, Parte final).

## Opción A — Todo con Docker Compose (recomendada)

```bash
# en la raíz EFT_Felipe_Penaloza/
docker compose up --build
```

Levanta la infraestructura + Kafka + Kafka-UI + 2 réplicas de cada microservicio de negocio.

### Escalado horizontal (criterio 6)

```bash
docker compose up -d --build --scale ms-pagos=3 --scale ms-cuentas=2 --scale ms-clientes=2
```

Verás en **Eureka** (http://localhost:8761) varias instancias registradas por servicio, y el **API Gateway** (lb://) repartiendo las peticiones entre ellas.

## Opción B — Local (sin Docker)

Arrancar en orden, cada uno en su carpeta con `mvn spring-boot:run`:
`config-server` → `eureka-server` → `auth-server` → `ms-cuentas` / `ms-pagos` / `ms-clientes` → `api-gateway`.
(Kafka local: `docker compose up kafka kafka-ui`; broker en `localhost:9094`.)

> Nota: el módulo `batch-service` (Parte 1) también usa el 8080 para su consola H2. Si corres el gateway y el batch a la vez en local, detén uno o cambia el puerto. En Docker no hay conflicto (el batch no va en el compose de servicios).

## Probar OAuth2 + los 3 servicios

```bash
# 1) Token (client_credentials)
curl -X POST http://localhost:9000/oauth2/token -u "bancoxyz-client:bancoxyz-secret" -d "grant_type=client_credentials"

TOKEN="<access_token>"
# 2) Servicios protegidos vía gateway
curl http://localhost:8080/cuentas  -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/clientes -H "Authorization: Bearer $TOKEN"

# 3) Registrar un pago -> dispara el evento Kafka
curl -X POST http://localhost:8080/pagos -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d "{\"cuentaId\":101,\"monto\":5000,\"tipo\":\"credito\"}"

# 4) Sin token -> 401 (demuestra la protección)
curl -i http://localhost:8080/cuentas
```

## Flujo de eventos (Kafka pub/sub)

```
ms-pagos  --publica PagoCreadoEvent--> topico "pagos"
                                          |--> ms-cuentas (grupo-cuentas): actualiza saldo
                                          |--> ms-clientes (grupo-clientes): notifica al cliente
```
Tras el POST /pagos, revisa los logs de ms-cuentas (saldo actualizado) y ms-clientes (notificación), y el tópico en **Kafka-UI** (http://localhost:8090).

## Evidencia a capturar

1. `docker compose up` con todos los contenedores arriba + `docker images` (imágenes `bancoxyz/*`).
2. Eureka con los servicios y, al escalar, **varias instancias** por servicio (criterio 6).
3. Token + llamada 200 vs 401 sin token (OAuth2).
4. POST /pagos y los logs de los 2 consumidores + el tópico en Kafka-UI.
5. Circuit breaker: `http://localhost:8081/actuator/circuitbreakers` (vía instancia o gateway).
