# Propuesta Técnica — Banco XYZ (Exp3 · Semana 8)

**Asignatura:** Desarrollo Backend III (PBY2203) · **Estudiante:** Felipe Peñaloza Oyarzún

## Contexto

El Banco XYZ migró su información legacy (Spring Batch) y construyó un ecosistema de microservicios con Spring Cloud. En esta etapa final el sistema se prepara para un **entorno Cloud productivo**: seguro, resiliente y portable.

## Decisiones técnicas

### 1. Seguridad: OAuth 2.0 con Authorization Server centralizado
Se reemplaza el JWT propio de la semana 7 (cada servicio firmaba y validaba sus tokens) por un **Authorization Server** dedicado (`auth-server`, Spring Authorization Server) que emite *access tokens* JWT firmados con RSA y publica su **JWK Set**. Cada microservicio pasa a ser **Resource Server** y solo valida la firma del token contra ese JWK.

**Justificación:** centraliza la autorización y aplica el principio de *delegación de seguridad* recomendado por la guía de la semana; es el patrón estándar de la industria para proteger sistemas distribuidos y evita duplicar lógica y secretos en cada servicio. Se eligió el flujo **client_credentials** por tratarse de comunicación máquina-a-máquina entre servicios/clientes de confianza (BFF, cajero, procesos internos), sin interfaz de login.

### 2. Conteinerización: una imagen por microservicio
Cada módulo incluye un `Dockerfile` **multi-stage**: una etapa compila con Maven y otra, ligera (solo JRE), ejecuta el JAR. Esto reduce el tamaño de la imagen final y garantiza portabilidad ("funciona igual en cualquier entorno").

### 3. Orquestación: Docker Compose
Un único `docker-compose.yml` levanta los 8 servicios Java + Kafka + Kafka-UI en una red común, resolviéndose por nombre de servicio. El orden de arranque se controla con `depends_on` + *healthchecks*, y el cliente de Config Server reintenta la conexión para tolerar las carreras de arranque.

### 4. Tolerancia a fallos: Resilience4j
Los endpoints de negocio usan `@CircuitBreaker` con método *fallback*: ante fallos de la capa de datos el sistema degrada de forma controlada en lugar de propagar el error.

### 5. Mensajería asíncrona: Apache Kafka
`ms-transacciones` publica eventos `TransaccionCreadaEvent`; `ms-cuentas` y `ms-tarjetas` los consumen de forma independiente y desacoplada (arquitectura orientada a eventos, pub/sub).

## Arquitectura resultante

```
Cliente ─▶ API Gateway (8080) ─▶ Resource Servers (ms-cuentas/transacciones/tarjetas)
                │                         ▲  valida JWT (JWK)
                └─▶ auth-server (9000) ───┘  emite JWT (client_credentials)
Config Server (8888) · Eureka (8761) · Kafka (bus de eventos)
```

## Preparación para producción (evolución futura)
Persistencia en un motor gestionado (MySQL/PostgreSQL) en lugar de H2; secretos fuera del código (Vault / variables de entorno); escalado horizontal de los consumidores Kafka; y validación del *issuer* y *scopes* por endpoint para autorización fina.
