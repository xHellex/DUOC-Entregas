# Banco XYZ — Desarrollo Backend Avanzado: Spring Cloud & Batch

**Evaluación Final Transversal (EFT) — Desarrollo Backend III (PBY2203)**
Felipe Peñaloza Oyarzún · DUOC UC

Modernización del sistema legacy (COBOL/mainframe) del Banco XYZ hacia una arquitectura de **microservicios en la nube**, resiliente y segura. El proyecto integra las tres capacidades del ramo: migración batch, patrón BFF y microservicios con Spring Cloud.

---

## 1. Procesos clave de la migración

La solución aborda los **5 procesos clave** de la modernización:

1. **Migración de procesos batch** legacy a Spring Batch (reportes, intereses, estados de cuenta).
2. **División del monolito en microservicios** (Cuentas, Pagos, Clientes).
3. **Patrón Backend for Frontend (BFF)** para web, móvil y cajero.
4. **Seguridad distribuida** con OAuth 2.0 (Spring Cloud Security).
5. **Mensajería asíncrona** con Apache Kafka (arquitectura orientada a eventos).

---

## 2. Arquitectura

```
                 ┌─────────── Clientes ───────────┐
                 │   Web        Móvil      Cajero  │
                 └────┬──────────┬───────────┬─────┘
                      ▼          ▼           ▼
                 ┌──────────────────────────────┐
                 │   BFF (3 canales, :8085)      │  respuestas optimizadas + auth por canal
                 └──────────────┬───────────────┘
                                ▼
                 ┌──────────────────────────────┐
                 │   API Gateway (:8080)         │  enrutamiento + balanceo (lb://)
                 └───┬───────────┬───────────┬───┘
                     ▼           ▼           ▼
             ms-cuentas     ms-pagos    ms-clientes     (Resource Servers OAuth2 + Resilience4j)
               (:8081)       (:8082)      (:8083)
                     │           │  publica    │
                     │           ▼             │
                     │     topico "pagos" (Kafka) ──► consumen cuentas y clientes
                     ▼
     Infra: Config Server (:8888) · Eureka (:8761) · Auth Server OAuth2 (:9000)

     Parte 1 (offline): Batch Service — migra los CSV legacy (fin_legacy_data)
```

### Requerimientos de negocio y decisiones (justificación)

| Requerimiento | Decisión arquitectónica | Por qué |
|---------------|-------------------------|---------|
| Escalabilidad ante demanda variable | Microservicios + escalado horizontal (réplicas en Docker, balanceo por Eureka/Gateway) | Permite escalar solo el servicio con carga, no todo el sistema |
| Tolerancia a fallos | Resilience4j (Circuit Breaker + fallback) | Un fallo en un servicio no derriba al resto |
| Seguridad en entorno distribuido | OAuth 2.0 con Authorization Server central + Resource Servers | Autorización centralizada, sin duplicar credenciales |
| Desacople entre servicios | Kafka (pub/sub) | El productor no conoce a los consumidores; se agregan sin tocar el origen |
| Eficiencia por canal | BFF (web/móvil/cajero) | Cada canal recibe solo los datos que necesita |

---

## 3. Estructura del repositorio

```
EFT_Felipe_Penaloza/
├── batch-service/     # Parte 1 — Spring Batch (3 jobs sobre fin_legacy_data)
├── bff/               # Parte 2 — BFF 3 canales (web/móvil/cajero)
├── config-server/     # Parte 3 — configuración centralizada (:8888)
├── eureka-server/     # Parte 3 — descubrimiento (:8761)
├── auth-server/       # Parte 3 — OAuth2 Authorization Server (:9000)
├── api-gateway/       # Parte 3 — puerta de entrada (:8080)
├── ms-cuentas/        # Parte 3 — Gestión de Cuentas (:8081)
├── ms-pagos/          # Parte 3 — Procesamiento de Pagos (:8082)
├── ms-clientes/       # Parte 3 — Gestión de Clientes (:8083)
├── config-repo/       # YMLs servidos por el Config Server
├── docker-compose.yml # Orquestación del ecosistema
├── instrucciones.md   # Cómo ejecutar y probar cada componente
└── despliegue.md      # Despliegue en la nube (AWS EC2)
```

## 4. Tecnologías

Java 17 · Spring Boot 3.4.1 · Spring Cloud 2024.0.0 · Spring Batch · Spring Authorization Server · OAuth2 Resource Server · Eureka · Spring Cloud Gateway · Config Server · Resilience4j · Apache Kafka (KRaft) · H2 · Docker / Docker Compose.

## 5. Puesta en marcha rápida

```bash
# Ecosistema de microservicios (requiere Docker Desktop corriendo)
docker compose up --build

# Migración batch (Parte 1), por separado
cd batch-service && mvn spring-boot:run
```

Detalle completo en **`instrucciones.md`**; despliegue cloud en **`despliegue.md`**.

## 6. Comparación con el sistema legacy

| Aspecto | Legacy (COBOL/mainframe) | Nuevo (Spring Cloud) |
|--------|--------------------------|----------------------|
| Escalabilidad | Vertical, costosa | Horizontal, por servicio |
| Tolerancia a fallos | Un fallo afecta todo | Circuit breakers aíslan fallos |
| Integración | Rígida | APIs REST + eventos Kafka |
| Seguridad | Centralizada y limitada | OAuth2 distribuida por servicio |
| Despliegue | Manual, monolítico | Contenedores orquestados |
| Procesos batch | Scripts shell | Spring Batch con reintentos y paralelismo |
