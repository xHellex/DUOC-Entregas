# EFT · Parte 2 — Patrón Backend for Frontend (BFF)

Módulo `bff`. Implementa el patrón BFF para los **3 canales** del Banco XYZ, cada uno con respuestas optimizadas y seguridad propia.

## Los 3 canales (criterio 4 — 15 pts)

| Canal | Prefijo | Rol | Optimización |
|-------|---------|-----|--------------|
| **Web** | `/api/web` | `WEB` | Datos completos (dashboard, tarjetas, historial) para interfaces ricas |
| **Móvil** | `/api/movil` | `MOVIL` | Respuestas ligeras (resumen, campos esenciales) para ahorrar ancho de banda |
| **Cajero** | `/api/cajero` | `CAJERO` | Mínimo y seguro: consulta de saldo y retiro |

- **Autenticación/autorización por canal:** cada canal exige su propio rol (HTTP Basic). Las credenciales de un canal no sirven en otro.
- **Operación crítica protegida:** el retiro (`POST /api/cajero/cuentas/{id}/retiro`) queda restringido al rol `CAJERO`.
- **Independencia:** cada BFF tiene su controller y DTOs propios; comparten un `core` (modelos, repositorios, servicios).

## Credenciales por canal

| Canal | Usuario | Clave |
|-------|---------|-------|
| Web | `web_user` | `web123` |
| Móvil | `movil_user` | `movil123` |
| Cajero | `cajero_user` | `cajero123` |

## Ejecutar

```bash
cd bff
mvn spring-boot:run
```

Queda en **http://localhost:8085** (puerto 8085 para no chocar con el API Gateway de la Parte 3, que usa 8080). Consola H2: `http://localhost:8085/h2-console` (JDBC `jdbc:h2:mem:bancoxyz`, user `sa`).

## Endpoints

**Web** (`web_user:web123`)
- `GET /api/web/cuentas` · `GET /api/web/cuentas/{id}`
- `GET /api/web/cuentas/{id}/dashboard` (agregación completa)
- `GET /api/web/cuentas/{id}/transacciones`

**Móvil** (`movil_user:movil123`)
- `GET /api/movil/cuentas` · `GET /api/movil/cuentas/{id}`
- `GET /api/movil/cuentas/{id}/resumen` (ligero)
- `GET /api/movil/cuentas/{id}/transacciones`

**Cajero** (`cajero_user:cajero123`)
- `GET /api/cajero/cuentas/{id}/saldo`
- `POST /api/cajero/cuentas/{id}/retiro`  (body JSON: `{ "monto": 20000 }`)

## Pruebas rápidas (PowerShell / curl)

```bash
# Web — dashboard completo
curl -u web_user:web123 http://localhost:8085/api/web/cuentas/1/dashboard

# Móvil — resumen ligero
curl -u movil_user:movil123 http://localhost:8085/api/movil/cuentas/1/resumen

# Cajero — saldo
curl -u cajero_user:cajero123 http://localhost:8085/api/cajero/cuentas/1/saldo

# Cajero — retiro (operación crítica)
curl -u cajero_user:cajero123 -H "Content-Type: application/json" -d "{\"monto\":20000}" http://localhost:8085/api/cajero/cuentas/1/retiro

# Seguridad por canal: credencial de un canal NO abre otro -> 403
curl -u movil_user:movil123 http://localhost:8085/api/web/cuentas
```

## Evidencia a capturar

1. Las 3 respuestas (web completa vs móvil ligera vs cajero mínima) para la **misma cuenta** — muestra la optimización por canal.
2. Un retiro exitoso por el canal cajero.
3. Un 401/403 al usar credenciales cruzadas (seguridad por canal).

## Nota de integración
En esta entrega el BFF es la capa de optimización/agregación por canal y es ejecutable de forma independiente (requisito de BFFs independientes). En la arquitectura final (informe técnico) el BFF se ubica delante del API Gateway y los microservicios de la Parte 3.
