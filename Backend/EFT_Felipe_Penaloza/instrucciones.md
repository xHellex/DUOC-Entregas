# Instrucciones para ejecutar y probar cada componente

Banco XYZ — EFT Desarrollo Backend III. Pasos para ejecutar y **verificar** cada parte.

## Requisitos

- Java 17+ (probado con JDK 22) y Maven.
- Docker Desktop (para el ecosistema de microservicios y Kafka).

---

## Parte 1 — Migración Batch (`batch-service`)

```bash
cd batch-service
mvn spring-boot:run
```

Ejecuta en orden los 3 jobs sobre el dataset oficial `fin_legacy_data`:

1. **Reporte de Transacciones Diarias** (`movimientos_financieros_diarios.csv`)
2. **Cálculo de Intereses** (`intereses_trimestrales.csv`)
3. **Estados de Cuenta Anuales** (`estados_financieros_anuales.csv`)

**Qué verificar:**
- En consola, los 3 jobs terminan en estado `COMPLETED`.
- Hilos `[Batch-Part-1/2/3]` → paralelismo.
- Líneas `[SKIP]` registrando anomalías (monto ≤ 0, tipo inválido, edad fuera de rango, etc.).
- Carpeta `salida/` con `resumen_transacciones_*.csv`, `informe_anual_*.csv` y `errores_*.csv`.
- Consola H2 en `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:bancoxyz`, user `sa`): tablas `TRANSACCION`, `CUENTA_INTERES`, `ESTADO_CUENTA_ANUAL`.

Escalabilidad: `mvn spring-boot:run -Dspring-boot.run.arguments=--app.grid.size=4` (compara tiempos con distinto nº de particiones).

> Detén el batch antes de levantar el gateway en local: ambos usan el 8080.

---

## Parte 2 — BFF 3 canales (`bff`)

```bash
cd bff
mvn spring-boot:run
```
Queda en `http://localhost:8085`.

**Credenciales por canal:** `web_user:web123` · `movil_user:movil123` · `cajero_user:cajero123`.

```bash
# Web (datos completos)
curl -u web_user:web123 http://localhost:8085/api/web/cuentas/101/dashboard
# Móvil (ligero)
curl -u movil_user:movil123 http://localhost:8085/api/movil/cuentas/101/resumen
# Cajero (saldo + retiro)
curl -u cajero_user:cajero123 http://localhost:8085/api/cajero/cuentas/101/saldo
curl -u cajero_user:cajero123 -H "Content-Type: application/json" -d "{\"monto\":20000}" http://localhost:8085/api/cajero/cuentas/101/retiro
# Seguridad por canal (credencial cruzada -> 403)
curl -u movil_user:movil123 http://localhost:8085/api/web/cuentas
```

**Qué verificar:** la misma cuenta devuelve respuestas distintas por canal (completa vs ligera vs mínima) y las credenciales de un canal no abren otro.

---

## Parte 3 — Microservicios (ecosistema completo)

```bash
# en la raíz del proyecto, con Docker Desktop corriendo
docker compose up --build
```

Espera a que todo quede arriba. Puntos de acceso: Eureka `:8761`, Gateway `:8080`, Auth `:9000`, Kafka-UI `:8090`.

### Probar OAuth2 + los 3 servicios

```bash
# 1) Obtener token (client_credentials)
curl -X POST http://localhost:9000/oauth2/token -u "bancoxyz-client:bancoxyz-secret" -d "grant_type=client_credentials"

$TOKEN = "<access_token>"   # en PowerShell
# 2) Servicios protegidos (vía gateway)
curl http://localhost:8080/cuentas  -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/clientes -H "Authorization: Bearer $TOKEN"
# 3) Registrar un pago (dispara evento Kafka)
curl -X POST http://localhost:8080/pagos -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"cuentaId\":101,\"monto\":5000,\"tipo\":\"credito\"}"
# 4) Sin token -> 401
curl -i http://localhost:8080/cuentas
```

**Qué verificar:**
- 200 con token, 401 sin token (OAuth2 protege los servicios).
- Tras el POST /pagos: en los logs de **ms-cuentas** se actualiza el saldo y en **ms-clientes** se notifica al cliente (2 consumidores, mismo evento). El tópico `pagos` se ve en **Kafka-UI** (`http://localhost:8090`).
- Resilience4j: `http://localhost:8081/actuator/circuitbreakers`.

### Escalabilidad horizontal

```bash
docker compose up -d --build --scale ms-pagos=3 --scale ms-cuentas=2 --scale ms-clientes=2
```
En Eureka (`:8761`) aparecen **varias instancias** por servicio; el gateway balancea entre ellas.

### Apagar

```bash
docker compose down
```
