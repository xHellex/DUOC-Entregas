# Backend for Frontend (BFF) - Banco XYZ

**Desarrollo Backend III (PBY2203) · Experiencia 2 · Semana 5**
Actividad sumativa individual: *Implementando el patrón arquitectónico Backend for Frontend (BFF)*

**Repositorio:** https://github.com/xHellex/DUOC-Entregas/tree/main/Backend/Exp2_S5

---

## 1. Objetivo del proyecto

Implementar el patrón **Backend for Frontend (BFF)** para el Banco XYZ, creando un backend especializado por cada tipo de cliente (web, móvil y cajero). Cada BFF **integra y agrega** información desde el servicio backend real y la **transforma** según las necesidades de su frontend, optimizando el tamaño de las respuestas y el consumo de recursos por canal.

## 2. Estrategia de implementación: cuatro aplicaciones completamente independientes

Siguiendo la pauta de la actividad sumativa, la arquitectura NO es un monolito con módulos internos: son **cuatro aplicaciones Spring Boot separadas**, cada una con su propio `pom.xml`, su propio proceso, su propio puerto y su propio ciclo de despliegue. No comparten código en tiempo de compilación (cada BFF define sus propios DTOs de forma duplicada) ni base de datos.

| Módulo | Rol | Puerto | Depende de |
|---|---|---|---|
| `banco-servicios` | Backend real: Cuentas, Transacciones, Tarjetas (persistencia H2 propia) | **9000** | — (no depende de ningún BFF) |
| `bff-web` | BFF del canal Web | **8081** | `banco-servicios` (solo por HTTP) |
| `bff-movil` | BFF del canal Móvil | **8082** | `banco-servicios` (solo por HTTP) |
| `bff-cajero` | BFF del canal Cajero (ATM) | **8083** | `banco-servicios` (solo por HTTP) |

Cada BFF puede compilarse, probarse, ejecutarse y desplegarse por separado sin tocar los demás módulos. La comunicación entre un BFF y `banco-servicios` es exclusivamente por **HTTP REST** (`RestClient` de Spring), tal como ocurriría entre microservicios reales en producción — nunca por inyección directa de un `@Service` en el mismo proceso.

Un `pom.xml` raíz agrupa los cuatro módulos únicamente como comodidad para compilarlos con un solo comando (`mvn -f pom.xml clean compile`); **no es un requisito de ejecución**: cada aplicación arranca de forma autónoma con su propio `mvn spring-boot:run` dentro de su carpeta.

## 3. banco-servicios: el servicio backend real

`banco-servicios` expone una API interna (`/api/interno/**`) consumida únicamente por los BFF. Organiza sus datos en tres dominios:

| Dominio | Responsabilidad |
|---|---|
| Cuentas | Datos de cuenta y operación de retiro con validación de saldo |
| Transacciones | Historial de movimientos por cuenta |
| Tarjetas | Tarjetas asociadas a cada cuenta |

Los BFF **agregan** estos tres recursos —obtenidos con llamadas HTTP independientes— para construir respuestas compuestas, evitando que el frontend final tenga que hacer múltiples llamadas:

| BFF | Endpoint agregado | Integra (vía HTTP a banco-servicios) |
|---|---|---|
| Web | `GET /api/web/cuentas/{id}/dashboard` | Cuenta + Transacciones + Tarjetas |
| Móvil | `GET /api/movil/cuentas/{id}/resumen` | Cuenta + conteo de tarjetas activas + últimas 3 transacciones |
| Cajero | `GET /api/cajero/cuentas/{id}/saldo` | Cuenta + número de tarjetas activas |

## 4. Optimización y transformación por canal

Cada canal recibe la información agregada pero **transformada y optimizada** para su contexto:

- **Web**: respuesta completa. El dashboard entrega todos los campos, historial completo y detalle de tarjetas.
- **Móvil**: respuesta ligera. El resumen entrega solo saldo, número de cuenta enmascarado, conteo de tarjetas activas (no el detalle) y las últimas 3 transacciones.
- **Cajero**: respuesta mínima. Solo saldo, cuenta enmascarada y tarjetas activas, sin datos personales del titular.

Ejemplo real capturado — cuenta 103, endpoint de saldo del cajero (la única tarjeta de esta cuenta está inactiva, prueba de que la agregación es real y no un valor fijo):
```json
{ "cuentaEnmascarada": "**** 5678", "saldoDisponible": 12000.0, "tarjetasActivas": 0 }
```

## 5. Seguridad por canal — ahora reforzada por independencia física

Cada BFF es un **proceso separado** y solo conoce las credenciales de su propio canal (no hay una única `SecurityConfig` compartida con los tres roles, como en un monolito). El aislamiento ya no depende solo de la autorización lógica (`hasRole`): el puerto de un canal directamente **no expone** las rutas de otro.

| Canal | Usuario | Contraseña | Rol | Puerto / Base |
|---|---|---|---|---|
| Web | `web_user` | `web123` | WEB | `:8081/api/web/**` |
| Móvil | `movil_user` | `movil123` | MOVIL | `:8082/api/movil/**` |
| Cajero | `cajero_user` | `cajero123` | CAJERO | `:8083/api/cajero/**` |

Dos formas de aislamiento verificadas (ver evidencia):
1. Credencial de un canal contra su propio proceso pero ruta ajena → no aplica (esa ruta no existe en ese proceso) → **404**.
2. Credencial de otro canal contra un proceso que no la conoce → **401**.

## 6. Estructura del código

```
Exp2_S5_Felipe_Penaloza/
├── pom.xml                      Agregador (solo para compilar los 4 módulos juntos)
├── banco-servicios/              Backend real — puerto 9000
│   ├── pom.xml
│   └── src/main/java/com/bancoxyz/bancoservicios/
│       ├── BancoServiciosApplication.java
│       ├── model/        Cuenta, Transaccion, Tarjeta
│       ├── repository/   (uno por entidad, Spring Data JPA)
│       ├── service/      CuentaService, TransaccionService, TarjetaService
│       ├── config/       DataInitializer
│       └── web/          InternoCuentaController (API /api/interno)
├── bff-web/                      BFF Web — puerto 8081
│   ├── pom.xml
│   └── src/main/java/com/bancoxyz/bffweb/
│       ├── BffWebApplication.java
│       ├── client/        BancoServiciosClient (RestClient) + DTOs de transporte
│       ├── web/           controller + dto (incluye DashboardWebDTO agregado)
│       └── security/      SecurityConfig (solo rol WEB)
├── bff-movil/                     BFF Móvil — puerto 8082 (misma forma que bff-web)
└── bff-cajero/                    BFF Cajero — puerto 8083 (incluye endpoint de retiro)
```

No existe un módulo `core` compartido entre los BFF: cada uno define su propia copia de los DTOs de transporte (`CuentaDTO`, `TransaccionDTO`, `TarjetaDTO`) que reflejan el contrato JSON de `banco-servicios`. Es una duplicación deliberada: preserva que cada módulo sea desplegable de forma verdaderamente independiente, sin un JAR compartido que acoplaría sus versiones.

## 7. Instrucciones de ejecución

Se necesitan **4 terminales**, uno por proceso (el orden importa: `banco-servicios` primero):

```bash
# Terminal 1
cd banco-servicios && mvn spring-boot:run      # arranca en :9000

# Terminal 2
cd bff-web && mvn spring-boot:run              # arranca en :8081

# Terminal 3
cd bff-movil && mvn spring-boot:run            # arranca en :8082

# Terminal 4
cd bff-cajero && mvn spring-boot:run           # arranca en :8083
```

Compilar y testear los 4 módulos de una vez (sin arrancarlos):
```bash
mvn -f pom.xml clean compile
mvn -f pom.xml test
```

### Probar la agregación por canal

```bash
# banco-servicios responde directo (backend real, sin pasar por ningún BFF)
curl http://localhost:9000/api/interno/cuentas/103

# Web: dashboard agregado (cuenta + transacciones + tarjetas)
curl -u web_user:web123 http://localhost:8081/api/web/cuentas/101/dashboard

# Móvil: resumen ligero agregado
curl -u movil_user:movil123 http://localhost:8082/api/movil/cuentas/101/resumen

# Cajero: saldo con tarjetas activas
curl -u cajero_user:cajero123 http://localhost:8083/api/cajero/cuentas/103/saldo

# Retiro (operación crítica, PowerShell requiere comillas simples en el body)
curl -u cajero_user:cajero123 -X POST http://localhost:8083/api/cajero/cuentas/101/retiro `
  -H "Content-Type: application/json" -d '{"monto": 500}'

# Aislamiento: credencial móvil contra el proceso web (no la conoce) -> 401
curl -u movil_user:movil123 http://localhost:8081/api/web/cuentas

# Aislamiento: el proceso web ni siquiera expone rutas de cajero -> 404
curl -u web_user:web123 http://localhost:8081/api/cajero/cuentas/101/saldo
```

## 8. Evidencia de ejecución

Ver `evidencia/00_compilacion_test.txt` (compilación y tests de los 4 módulos vía `mvn -f pom.xml`) y `evidencia/01_pruebas_bff.txt` (los 4 procesos corriendo simultáneamente: dashboard web agregado, resumen móvil, saldo de cajero con `tarjetasActivas:0` para la cuenta 103, retiro exitoso, retiro rechazado por saldo insuficiente [409] y por monto inválido [400], y las dos formas de aislamiento de canal [401 y 404]).

## 9. Autor

Felipe Peñaloza — Desarrollo Backend III (PBY2203)
