# Tolerancia a Fallos y Arquitectura de Eventos - Banco XYZ (Spring Cloud + Kafka)

**Desarrollo Backend III (PBY2203) · Experiencia 3 · Semana 7**
Actividad formativa grupal: *Configurando tolerancia a fallos y arquitectura de eventos con microservicios en la nube*

**Repositorio:** https://github.com/xHellex/DUOC-Entregas/tree/main/Backend/Exp3_S7

---

## 1. Objetivo del proyecto

Fortalecer el ecosistema de microservicios del Banco XYZ (semana 6) incorporando una **arquitectura de eventos asíncrona** con Apache Kafka y manteniendo la **tolerancia a fallos con Resilience4j**. Al registrar una transacción se emite un evento que otros microservicios consumen de forma independiente.

## 2. Arquitectura de eventos elegida

Patrón **Publish/Subscribe** con Apache Kafka. Diagrama completo en `evidencia/diagrama_arquitectura_eventos.png` (fuente editable: `.svg`).

```
ms-transacciones (PRODUCTOR)
      │  publica TransaccionCreadaEvent   (key = cuentaId)
      ▼
  Tópico Kafka "transacciones"  (3 particiones)
  { transaccionId, cuentaId, monto, tipo, fecha }
      │
      ├──► ms-cuentas   (groupId = grupo-cuentas)   → actualiza el saldo
      └──► ms-tarjetas  (groupId = grupo-tarjetas)  → registra el movimiento
```

**Justificación.** Una transacción es un hecho del negocio que interesa a varios servicios a la vez. Publicarla como evento desacopla al productor de los consumidores: cada uno reacciona por su cuenta y se pueden sumar consumidores sin tocar al productor.

- **Un `groupId` por microservicio**: cada grupo recibe *todos* los eventos (broadcast).
- **Tópico con 3 particiones**: dentro de un grupo, las instancias se reparten las particiones (escala horizontal, hasta 3 instancias activas por grupo).
- **Key = `cuentaId`**: las transacciones de una misma cuenta caen siempre en la misma partición y conservan su orden.
- **Contrato JSON puro**: sin header `__TypeId__` y con la fecha en formato ISO; cada servicio tiene su propia copia del `record` del evento (no comparten código).

### Rol del API Gateway

Es un componente adicional respecto de lo pedido en la actividad, heredado de la semana 6. **No participa en el flujo de eventos**: es la puerta de entrada única para los clientes HTTP (una sola dirección, `localhost:8080`), enruta por nombre lógico (`lb://ms-transacciones`) usando Eureka y reenvía el header `Authorization` para que cada microservicio valide el JWT. En esta semana es por donde entra el `POST /transacciones` que origina el evento; desde ahí, productor → Kafka → consumidores es asíncrono y no pasa por el Gateway.

## 3. Componentes

| Componente | Puerto | Rol |
|---|---|---|
| config-server | 8888 | Configuración centralizada (`config-repo/`) |
| eureka-server | 8761 | Descubrimiento de servicios |
| api-gateway | 8080 | Puerta de enlace única |
| ms-cuentas | 8081 | Consumidor de eventos → actualiza el saldo |
| ms-transacciones | 8082 | **Productor de eventos** |
| ms-tarjetas | 8083 | Consumidor de eventos → registra el movimiento |
| Kafka (KRaft) | 9092 | Broker de mensajería (imagen `apache/kafka:3.9.0`) |
| Kafka-UI | 8090 | Interfaz visual del broker |

## 4. Tolerancia a fallos

**Resilience4j.** Cada microservicio mantiene su Circuit Breaker (`cuentasCB`, `transaccionesCB`, `tarjetasCB`) con método *fallback*.

**Resiliencia de la arquitectura de eventos** (probada en ejecución real, ver `evidencia/03_*` y `evidencia/05_*`):

- *Broker caído*: el productor sigue respondiendo y el evento queda en su buffer; al volver el broker se entrega a los consumidores.
- *Consumidor caído*: no afecta al productor ni a los demás consumidores; los eventos esperan en el tópico y el consumidor se pone al día al reiniciar (el lag vuelve a 0).
- *Circuit Breaker abierto*: con el circuito de las consultas abierto, el registro de transacciones y su evento siguen funcionando.

**Limitaciones conocidas:** si el broker está caído más de `delivery.timeout.ms` (120 s) o el productor muere con eventos en su buffer, esos eventos se pierden (no hay patrón *Outbox*); la entrega es *at-least-once* y los consumidores no son idempotentes; falta un tópico *dead-letter* para mensajes que no se puedan deserializar.

## 5. Instrucciones de ejecución

### Requisitos
- JDK 17+, Maven 3.9+, Docker con Compose.

### Paso 1: levantar Kafka

```bash
docker compose up -d
```

Levanta el broker (`localhost:9092`), Kafka-UI (`http://localhost:8090`) y crea el tópico `transacciones` con 3 particiones (servicio `kafka-init`).

El broker anuncia dos listeners: `EXTERNAL` (`localhost:9092`, para los microservicios en el host) e `INTERNAL` (`kafka:29092`, para Kafka-UI). Si Kafka se despliega en otra máquina (p. ej. un homelab), reemplazar `localhost` en `KAFKA_ADVERTISED_LISTENERS` (listener `EXTERNAL`) por la IP del servidor y actualizar `bootstrap-servers` en `config-repo/*.yml`.

### Paso 2: levantar el ecosistema (en orden, cada uno en su terminal)

```bash
cd config-server && mvn spring-boot:run
cd eureka-server && mvn spring-boot:run
cd ms-cuentas && mvn spring-boot:run
cd ms-transacciones && mvn spring-boot:run
cd ms-tarjetas && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

Para probar la escalabilidad, una segunda instancia de un consumidor en otro puerto (mismo `groupId`):

```bash
cd ms-tarjetas && mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8093"
```

### Paso 3: probar el flujo de eventos

```bash
# 1. Obtener token JWT
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"usuario":"admin","clave":"admin123"}'

# 2. Registrar una transacción (dispara el evento)
curl -X POST http://localhost:8080/transacciones -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"cuentaId":101,"monto":500,"tipo":"credito"}'

# 3. Ver el saldo actualizado por el consumidor (5000 -> 5500)
curl http://localhost:8080/cuentas/101 -H "Authorization: Bearer <TOKEN>"
```

> En PowerShell usar `curl.exe` (no `curl`) y dejar el JSON entre comillas simples, como arriba.

En las consolas de `ms-cuentas` y `ms-tarjetas` aparecen los logs `[KAFKA-CONSUMER ...]`, y en Kafka-UI se ve el tópico `transacciones` con sus mensajes. Tras un reinicio de los microservicios, el API Gateway puede tardar hasta ~30 s en enrutar (refresco del registro de Eureka).

### Pruebas automatizadas

```bash
cd <modulo> && mvn test        # en cada uno de los 6 módulos
```

12 pruebas: carga de contexto en cada módulo y, en los tres microservicios, pruebas del **contrato del evento** (JSON puro, fecha ISO, sin `__TypeId__`).

## 6. Estructura del proyecto

```
Exp3_S7_Grupo3/
├── docker-compose.yml      Kafka (KRaft) + creación del tópico + Kafka-UI
├── config-server/  eureka-server/  api-gateway/
├── ms-transacciones/       Productor (event/TransaccionEventProducer)
├── ms-cuentas/             Consumidor (event/TransaccionEventConsumer)
├── ms-tarjetas/            Consumidor (event/TransaccionEventConsumer)
├── config-repo/            Configuración de cada ms (incluye Kafka)
├── evidencia/              Salidas reales de ejecución y diagrama
└── informe.md              Fuente del informe (Informe_Exp3_S7_Grupo3.docx)
```

## 7. Evidencia de ejecución

| Archivo | Contenido |
|---|---|
| `evidencia/diagrama_arquitectura_eventos.png` | Diagrama de la arquitectura de eventos |
| `evidencia/00_compilacion_tests.txt` | `mvn clean test` de los 6 módulos (12 pruebas, 0 fallas) |
| `evidencia/01_kafka_topico.txt` | Broker, tópico (3 particiones), grupos y mensajes reales |
| `evidencia/02_flujo_eventos_kafka.txt` | Flujo productor → Kafka → consumidores; saldo 5000 → 5500 → 5200 |
| `evidencia/03_resiliencia_consumidor_y_broker.txt` | Broker caído y consumidor caído (lag 2 → 0) |
| `evidencia/04_escalabilidad_particiones.txt` | 2 instancias de un grupo repartiéndose 12 eventos sin duplicados |
| `evidencia/05_circuit_breaker_con_eventos.txt` | Circuit Breaker CLOSED → OPEN → CLOSED con falla real |

## 8. Integrantes

Grupo 3 — Desarrollo Backend III (PBY2203)
