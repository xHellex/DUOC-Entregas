# Guion del video — EFT Banco XYZ (5–7 min)

Presentación grabada en MP4 con webcam (Kaltura). Cubre los 4 puntos exigidos:
resumen ejecutivo, resultados y comparación con el legacy, desafíos y
soluciones, y propuestas de mejora.

---

## Preparación ANTES de grabar (deja todo abierto, no lo armes en cámara)

**Local (Docker Desktop corriendo):**
```bash
cd EFT_Felipe_Penaloza
docker compose up --build -d
```
Espera a que `docker compose ps` muestre todo `Up`/`healthy` antes de grabar.

**Batch (Parte 1) — corre ANTES de grabar, deja la consola con el scrollback:**
```bash
cd batch-service
mvn spring-boot:run
```
No lo corras en vivo durante la grabación (toma ~20-30s de arranque); muestra
la terminal ya con los 3 Jobs `COMPLETED` cuando llegue el momento.

**Pestañas del navegador, en este orden de pestaña:**
1. Diagrama de arquitectura (informe PDF, sección 3).
2. Eureka local — `http://localhost:8761`
3. **Consola AWS EC2** — lista de instancias (la que ya tienes).
4. **Eureka por IP pública** — `http://54.162.151.124:8761`
5. Kafka-UI local — `http://localhost:8090`
6. Tabla de comparación con el legacy (informe, sección 8).

**Terminal para comandos en vivo (PowerShell, variables ya listas):**
```powershell
$TOKEN = $null   # lo pides en vivo durante la grabación
```

**Importante:** cuando termines de grabar, **termina la instancia EC2**
(Acciones → Finalizar instancia) para dejar de pagar. No la necesitas más
una vez grabado el video.

---

## Minuto a minuto

### 0:00–0:25 · Intro (webcam)
"Hola, soy Felipe Peñaloza. Les presento la Evaluación Final Transversal de
Desarrollo Backend III: la modernización del sistema legacy del Banco XYZ
hacia una arquitectura de microservicios en la nube con Spring Cloud y
Spring Batch."

### 0:25–1:10 · Resumen ejecutivo (pantalla: diagrama de arquitectura)
- Objetivo: migrar un monolito COBOL/mainframe a microservicios resilientes,
  seguros y escalables.
- Alcance: tres partes — migración batch, patrón BFF (web/móvil/cajero) y
  microservicios (Cuentas, Pagos, Clientes) con Spring Cloud.
- Señala en el diagrama: Config Server, Eureka, Auth Server OAuth2, API
  Gateway, Kafka.

### 1:10–1:40 · Batch (pantalla: terminal ya con el output)
Desplázate por la consola ya corrida. Menciona:
- 3 Jobs `COMPLETED`, hilos `[Batch-Part-1/2/3]` → paralelismo.
- ~392 transacciones válidas, ~340 intereses, ~952 estados anuales.
- Líneas `[SKIP]` → manejo de errores sobre datos legacy con anomalías.

### 1:40–2:10 · BFF 3 canales (pantalla: terminal, comandos ya preparados)
```bash
curl -u web_user:web123 http://localhost:8085/api/web/cuentas/1/dashboard
curl -u movil_user:movil123 http://localhost:8085/api/movil/cuentas/1/resumen
curl -u cajero_user:cajero123 http://localhost:8085/api/cajero/cuentas/1/saldo
```
Muestra que la misma cuenta devuelve datos completos (web) vs ligeros
(móvil) vs mínimos (cajero) — seguridad y respuesta distinta por canal.

### 2:10–2:40 · Microservicios + escalabilidad horizontal (pantalla: terminal + Eureka local)
```bash
docker compose ps
```
Señala las 2 réplicas de ms-cuentas/ms-pagos/ms-clientes. Cambia a la pestaña
de Eureka local (`:8761`) y muestra las instancias registradas.

### 2:40–3:30 · Despliegue real en la nube — AWS EC2 (pantalla: consola AWS + Eureka por IP pública)
**Este es el punto que quedó pendiente en la entrega anterior — muéstralo con confianza.**
- Pestaña consola EC2: instancia `server-backend` **En ejecución**, tipo
  `t3.large`, IP pública `54.162.151.124`.
- Pestaña Eureka por IP pública (`http://54.162.151.124:8761`): los mismos
  5 servicios, con réplicas, corriendo de verdad en la nube — no solo en tu
  equipo.
- Narra: "El mismo ecosistema Docker que corre en mi máquina, corre igual en
  una instancia EC2 real, con el mismo docker-compose."

### 3:30–4:15 · OAuth2 + Kafka en vivo (pantalla: terminal, contra la IP pública o localhost)
```powershell
curl.exe -X POST http://54.162.151.124:9000/oauth2/token -u "bancoxyz-client:bancoxyz-secret" -d "grant_type=client_credentials"
$TOKEN = "<pega el access_token>"
curl.exe http://54.162.151.124:8080/cuentas -H "Authorization: Bearer $TOKEN"
curl.exe -i http://54.162.151.124:8080/cuentas          # sin token -> 401
```
Luego dispara un pago y muestra Kafka-UI:
```powershell
$body = @{ cuentaId = 101; monto = 1000; tipo = "credito" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://54.162.151.124:8080/pagos" -Headers @{ Authorization = "Bearer $TOKEN" } -ContentType "application/json" -Body $body
```
Cambia a la pestaña Kafka-UI y muestra el mensaje nuevo en el tópico `pagos`.

### 4:15–4:45 · Resilience4j (pantalla: actuator o narrado sobre el informe)
Explica el mecanismo (Circuit Breaker + fallback) y muestra el estado
`CLOSED` del circuito en `/actuator/circuitbreakers`. Si ya tienes el video
de la transición CLOSED→OPEN de una prueba anterior, puedes narrarlo en vez
de repetirlo en vivo para no alargar el video.

### 4:45–5:30 · Comparación con el sistema legacy (pantalla: tabla del informe)
- Escalabilidad: vertical y costosa → horizontal, por servicio (lo que
  acabas de mostrar en EC2).
- Tolerancia a fallos: un fallo tumbaba todo → circuit breakers aíslan.
- Seguridad: centralizada y limitada → OAuth2 distribuida por servicio.

### 5:30–6:15 · Desafíos enfrentados y soluciones
- Datos legacy con anomalías → validación + Skip/Retry en Spring Batch.
- Seguridad sin acoplar credenciales → Authorization Server OAuth2 propio.
- Arranque ordenado en Docker → healthchecks + depends_on + reintentos del
  Config Client (el bug real de `spring.config.import` que encontraste y
  corregiste).
- Comunicación desacoplada entre microservicios independientes → Kafka
  pub/sub (y el bug real del header `__TypeId__` que también corregiste).

### 6:15–7:00 · Propuestas de mejora y cierre (webcam)
- Persistencia gestionada (RDS en vez de H2), secretos en Secrets Manager,
  orquestación con ECS/EKS en vez de un solo EC2, observabilidad con
  CloudWatch.
- Cierre: "Con esto el Banco XYZ queda con una base moderna, segura y
  escalable, desplegada y verificada en la nube real. Gracias."

---

## Checklist de grabación
- [ ] Stack local (`docker compose up`) y EC2 ambos arriba antes de grabar.
- [ ] Batch ya corrido, consola con el scrollback listo.
- [ ] Las 6 pestañas abiertas en el orden indicado.
- [ ] Webcam visible en intro y cierre.
- [ ] Duración entre 5 y 7 minutos.
- [ ] Se abordan los 4 puntos (resumen, resultados+comparación, desafíos+soluciones, mejoras).
- [ ] Audio claro; formato MP4; subido por Kaltura.
- [ ] **Terminar la instancia EC2 apenas termines de grabar.**
