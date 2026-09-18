# Microservicios y Seguridad en la Nube - Banco XYZ (Spring Cloud)

**Desarrollo Backend III (PBY2203) · Experiencia 3 · Semana 6**
Actividad formativa grupal: *Implementando microservicios y seguridad en la nube con Spring Cloud*

**Repositorio:** https://github.com/xHellex/DUOC-Entregas/tree/main/Backend/Exp3_S6

---

## 1. Objetivo del proyecto

Migrar la solución del Banco XYZ a una arquitectura de **microservicios en la nube** con Spring Cloud, incorporando configuración centralizada, descubrimiento de servicios, enrutamiento por gateway, tolerancia a fallos y seguridad con JWT. El sistema expone como APIs el resultado de la migración de datos (cuentas, transacciones y tarjetas).

## 2. Arquitectura del ecosistema

El sistema se compone de seis aplicaciones Spring Boot independientes:

| Componente | Puerto | Rol |
|---|---|---|
| **config-server** | 8888 | Configuración centralizada (Spring Cloud Config) |
| **eureka-server** | 8761 | Descubrimiento de servicios (Netflix Eureka) |
| **api-gateway** | 8080 | Puerta de enlace única (Spring Cloud Gateway) |
| **ms-cuentas** | 8081 | Microservicio de cuentas (+ Resilience4j + JWT) |
| **ms-transacciones** | 8082 | Microservicio de transacciones (+ Resilience4j + JWT) |
| **ms-tarjetas** | 8083 | Microservicio de tarjetas (+ Resilience4j + JWT) |

```
Cliente ──► API Gateway (8080) ──► [ms-cuentas | ms-transacciones | ms-tarjetas]
                                          │
              todos se registran en ──► Eureka (8761)
              todos toman su config de ──► Config Server (8888)
```

## 3. Componentes de Spring Cloud implementados

### 3.1 Config Server (configuración centralizada)
Sirve la configuración de cada microservicio desde la carpeta `config-repo/` (modo `native`). Cada microservicio, al arrancar, descarga su archivo `ms-<nombre>.yml` desde `http://localhost:8888`. Esto centraliza puertos, datasources y parámetros en un solo lugar.

### 3.2 Eureka (Service Discovery)
Los tres microservicios y el gateway se registran en Eureka al arrancar. Así se encuentran entre sí por su **nombre lógico** (`lb://ms-cuentas`) en lugar de URLs fijas. El panel de Eureka (`http://localhost:8761`) muestra los servicios registrados.

### 3.3 API Gateway
Punto de entrada único. Enruta según la ruta de la petición hacia el microservicio correcto, descubriéndolo por Eureka:
- `/cuentas/**`, `/auth/**` → ms-cuentas
- `/transacciones/**` → ms-transacciones
- `/tarjetas/**` → ms-tarjetas

### 3.4 Tolerancia a fallos (Resilience4j)
Cada microservicio protege sus endpoints con un **Circuit Breaker**. Si el acceso a datos falla repetidamente, el circuito se abre y las peticiones se derivan a un método **fallback** que devuelve una respuesta de respaldo, evitando que el fallo se propague (resiliencia).

### 3.5 Seguridad (JWT)
Los **tres microservicios** implementan su propia autenticación: cada uno expone `/auth/login` (credenciales `admin` / `admin123`) y valida el token en sus endpoints protegidos vía `JwtAuthFilter`, de forma stateless. Al compartir el mismo `jwt.secret` (servido por el Config Server desde `config-repo/`), un token emitido por cualquiera de los tres es válido en los otros dos — cada microservicio sigue siendo capaz de autenticar por sí mismo, sin depender de que otro esté arriba. El API Gateway solo enruta `/auth/**` hacia `ms-cuentas` por simplicidad de un único punto de login para el cliente externo.

## 4. Instrucciones de ejecución

### Requisitos
- JDK 17+, Maven 3.9+

### Orden de arranque (importante)

Los componentes deben levantarse **en este orden**, cada uno en su propia terminal:

```bash
# 1. Config Server (primero: los demás dependen de él)
cd config-server && mvn spring-boot:run

# 2. Eureka Server
cd eureka-server && mvn spring-boot:run

# 3. Los tres microservicios (en cualquier orden entre ellos)
cd ms-cuentas && mvn spring-boot:run
cd ms-transacciones && mvn spring-boot:run
cd ms-tarjetas && mvn spring-boot:run

# 4. API Gateway (último)
cd api-gateway && mvn spring-boot:run
```

Verifica en `http://localhost:8761` que los cuatro servicios (gateway + 3 ms) aparezcan registrados.

### Probar el sistema

```bash
# 1. Obtener token JWT
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"usuario\":\"admin\",\"clave\":\"admin123\"}"

# 2. Consumir un microservicio con el token (vía gateway)
curl http://localhost:8080/cuentas -H "Authorization: Bearer <TOKEN>"
curl http://localhost:8080/transacciones -H "Authorization: Bearer <TOKEN>"
curl http://localhost:8080/tarjetas -H "Authorization: Bearer <TOKEN>"

# 3. Sin token → 401/403 (demuestra la seguridad)
curl http://localhost:8080/cuentas
```

## 5. Estructura del proyecto

```
Exp3_S6_Grupo3/
├── config-server/      Configuración centralizada
├── eureka-server/      Service discovery
├── api-gateway/        Puerta de enlace
├── ms-cuentas/         Microservicio + JWT + Resilience4j
├── ms-transacciones/   Microservicio + Resilience4j
├── ms-tarjetas/        Microservicio + Resilience4j
└── config-repo/        Archivos de configuración servidos por el config server
    ├── ms-cuentas.yml
    ├── ms-transacciones.yml
    └── ms-tarjetas.yml
```

## 6. Evidencia de ejecución

Ver `evidencia/00_compilacion.txt` (compilación limpia de los 6 módulos) y `evidencia/01_pruebas_ecosistema.txt` (los 6 procesos corriendo a la vez: Config Server sirviendo la configuración real de `ms-cuentas`, los 4 servicios registrados y `UP` en Eureka, login JWT vía gateway, los tres microservicios respondiendo vía gateway con el mismo token, acceso denegado sin token y con token inválido, y los tres Circuit Breaker registrados en estado `CLOSED`).

## 7. Integrantes

Grupo 3 — Desarrollo Backend III (PBY2203)
