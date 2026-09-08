# Backend for Frontend (BFF) - Banco XYZ

**Desarrollo Backend III (PBY2203) · Experiencia 2 · Semana 4**
Actividad: *Analizando el patrón arquitectónico con Backend for Frontend (BFF)*

**Repositorio:** https://github.com/xHellex/DUOC-Entregas/tree/main/Backend/Exp2_S4

---

## 1. Objetivo del proyecto

Implementar el patrón arquitectónico **Backend for Frontend (BFF)** para el Banco XYZ, creando un backend especializado para cada tipo de cliente (web, móvil y cajero automático). Cada BFF expone los mismos datos del banco pero adaptados a las necesidades específicas de su frontend, mejorando el rendimiento y la experiencia de usuario en cada canal.

## 2. ¿Qué es el patrón BFF?

En una arquitectura tradicional, un único backend atiende a todos los frontends por igual. Esto genera problemas cuando los clientes tienen necesidades distintas: un navegador de escritorio puede mostrar información rica y completa, pero un móvil necesita respuestas ligeras para ahorrar ancho de banda, y un cajero requiere una interfaz mínima y segura centrada en operaciones críticas.

El patrón BFF resuelve esto creando **un backend a medida para cada frontend**. La lógica de negocio se mantiene única y centralizada; lo que cambia en cada BFF es **cómo se transforma y expone la respuesta** para su cliente.

## 3. Estrategia de implementación elegida

El material describe tres estrategias posibles. Para este proyecto se eligió **un único servicio Spring Boot con módulos separados por canal**, en lugar de tres proyectos independientes.

**Justificación:** para el alcance del Banco XYZ, tres repositorios separados introducirían duplicación de infraestructura (tres despliegues, tres configuraciones de base de datos) sin un beneficio real, ya que los tres BFF consumen la misma fuente de datos. Los módulos separados dentro de un proyecto logran el objetivo del patrón —separación clara de la lógica por cliente— manteniendo un solo punto de despliegue y evitando duplicar la lógica de negocio, que queda en un `core` compartido. Esta estrategia corresponde al "diseño de endpoints personalizados" sobre una base común, aislando la lógica específica de cada cliente en su propio módulo.

## 4. Arquitectura

```
                    ┌─────────────────────────────┐
   Cliente Web ───► │  /api/web    (BFF Web)       │──┐
                    ├─────────────────────────────┤  │
 Cliente Móvil ───► │  /api/movil  (BFF Móvil)     │──┼──► BancoService ──► BD
                    ├─────────────────────────────┤  │   (lógica única)
   Cajero ATM  ───► │  /api/cajero (BFF Cajero)    │──┘
                    └─────────────────────────────┘
```

Cada BFF tiene su propio controller y sus propios DTOs, y transforma los datos del `BancoService` (compartido) según su cliente.

| BFF | Ruta | Característica |
|---|---|---|
| **Web** | `/api/web` | Respuestas **completas**: todos los campos, número de cuenta completo, historial extenso. |
| **Móvil** | `/api/movil` | Respuestas **ligeras**: campos esenciales, número de cuenta enmascarado, historial limitado a 5. |
| **Cajero** | `/api/cajero` | **Operaciones críticas**: consulta de saldo (sin datos personales) y retiro validado. |

## 5. Personalización por canal (ejemplos)

La misma cuenta 101, según el canal:

**Web** — `GET /api/web/cuentas/101`
```json
{ "id":101, "titular":"John Doe", "tipo":"ahorro", "saldo":5000.0, "numeroCuenta":"1234567890123456" }
```

**Móvil** — `GET /api/movil/cuentas/101` (ligero, enmascarado, sin tipo)
```json
{ "id":101, "titular":"John Doe", "saldo":5000.0, "cuentaEnmascarada":"**** 3456" }
```

**Cajero** — `GET /api/cajero/cuentas/101/saldo` (mínimo, sin datos personales)
```json
{ "cuentaEnmascarada":"**** 3456", "saldoDisponible":5000.0 }
```

## 6. Seguridad por canal

Cada BFF tiene su propio rol y credenciales; las de un canal no funcionan en otro (autenticación básica HTTP):

| Canal | Usuario | Contraseña | Rol | Acceso |
|---|---|---|---|---|
| Web | `web_user` | `web123` | WEB | `/api/web/**` |
| Móvil | `movil_user` | `movil123` | MOVIL | `/api/movil/**` |
| Cajero | `cajero_user` | `cajero123` | CAJERO | `/api/cajero/**` |

## 7. Estructura del código

```
Exp2_S4_Grupo3/
└── src/main/java/com/bancoxyz/bff/
    ├── BffBancoxyzApplication.java
    ├── core/                          ★ Núcleo compartido (lógica única)
    │   ├── model/    Cuenta, Transaccion
    │   ├── repository/  CuentaRepository, TransaccionRepository
    │   ├── service/  BancoService
    │   └── config/   DataInitializer
    ├── web/                           ★ BFF Web (completo)
    │   ├── controller/WebBffController
    │   └── dto/  CuentaWebDTO, TransaccionWebDTO
    ├── movil/                         ★ BFF Móvil (ligero)
    │   ├── controller/MovilBffController
    │   └── dto/  CuentaMovilDTO, TransaccionMovilDTO
    ├── cajero/                        ★ BFF Cajero (operaciones críticas)
    │   ├── controller/CajeroBffController
    │   └── dto/  SaldoCajeroDTO, RetiroRequest, RetiroResponse
    └── security/  SecurityConfig
```

## 8. Instrucciones de ejecución

```bash
mvn clean spring-boot:run
```

La aplicación arranca en `http://localhost:8080` y carga datos de ejemplo automáticamente.

### Probar cada BFF (autenticación básica HTTP)

En **Windows PowerShell** usar `curl.exe` (el binario real; `curl` a secas es un alias de `Invoke-WebRequest` y no entiende `-u`). En Git Bash / Linux / macOS usar `curl`.

```powershell
# BFF Web  — respuesta completa (todos los campos, número de cuenta entero)
curl.exe -u web_user:web123 http://localhost:8080/api/web/cuentas/101

# BFF Móvil — respuesta ligera (sin 'tipo', número enmascarado, historial recortado)
curl.exe -u movil_user:movil123 http://localhost:8080/api/movil/cuentas/101

# BFF Cajero — consulta de saldo (mínima, sin datos personales)
curl.exe -u cajero_user:cajero123 http://localhost:8080/api/cajero/cuentas/101/saldo

# BFF Cajero — retiro válido (saldo 5000 → 4000)
curl.exe -u cajero_user:cajero123 -X POST http://localhost:8080/api/cajero/cuentas/101/retiro `
  -H "Content-Type: application/json" -d '{"monto": 1000}'

# BFF Cajero — retiro que supera el saldo → { "exito": false, "mensaje": "Saldo insuficiente" }
curl.exe -u cajero_user:cajero123 -X POST http://localhost:8080/api/cajero/cuentas/101/retiro `
  -H "Content-Type: application/json" -d '{"monto": 999999}'

# Aislamiento por canal — credenciales WEB contra ruta MÓVIL → 403
curl.exe -s -o NUL -w "%{http_code}`n" -u web_user:web123 http://localhost:8080/api/movil/cuentas/101

# Sin credenciales → 401
curl.exe -s -o NUL -w "%{http_code}`n" http://localhost:8080/api/web/cuentas/101

# Historial: web completo vs móvil recortado (mismo dato)
curl.exe -u web_user:web123   http://localhost:8080/api/web/cuentas/101/transacciones
curl.exe -u movil_user:movil123 http://localhost:8080/api/movil/cuentas/101/transacciones
```

> El cuerpo JSON va entre **comillas simples** (`'{"monto": 1000}'`). No usar `"{\"monto\": 1000}"`: PowerShell no interpreta `\"` y el servidor responde `400 Bad Request`.

## 9. Evidencia de ejecución

La carpeta `evidencia/` contiene `01_pruebas_bff.txt` con la salida real de todos los llamados anteriores: la misma cuenta 101 en los tres formatos, el retiro válido y el rechazado por saldo insuficiente, y los códigos 403 / 401 del aislamiento por canal.

## 10. Integrantes

Grupo 3 — Felipe Peñaloza · Desarrollo Backend III (PBY2203)
