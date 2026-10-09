# Guion del video — EFT Banco XYZ (5–7 min)

Presentación grabada en MP4 con webcam (Kaltura). Cubre los 4 puntos exigidos: resumen ejecutivo, resultados y comparación con el legacy, desafíos y soluciones, y propuestas de mejora. Tiempos aproximados para quedar en 6 min.

> Tip: ten el stack ya levantado (`docker compose ps` en verde) y las pestañas abiertas (Eureka :8761, Kafka-UI :8090) antes de grabar, para mostrar en vivo.

---

### 0:00–0:30 · Presentación e intro (webcam)
"Hola, soy Felipe Peñaloza. Les presento la Evaluación Final Transversal de Desarrollo Backend III: la modernización del sistema legacy del Banco XYZ hacia una arquitectura de microservicios en la nube con Spring Cloud y Spring Batch."

### 0:30–1:30 · Resumen ejecutivo del proyecto
- Objetivo: migrar un monolito COBOL/mainframe a microservicios resilientes, seguros y escalables.
- Alcance: tres partes — migración batch, patrón BFF (web/móvil/cajero) y microservicios (Cuentas, Pagos, Clientes).
- Muestra el diagrama de arquitectura (Figura 1 del informe) y nombra los componentes: Config Server, Eureka, Auth Server OAuth2, API Gateway, Kafka.

### 1:30–3:30 · Resultados obtenidos (demo en pantalla)
1. **Batch (Parte 1):** muestra la consola con los 3 Jobs COMPLETED, los hilos de paralelismo y los registros omitidos (skip). Menciona: ~392, ~340 y ~952 registros válidos.
2. **Microservicios (Parte 3):** `docker compose ps` con las réplicas; Eureka con las instancias registradas.
3. **OAuth2:** pide un token y muestra una llamada con token (200) vs sin token (401).
4. **Kafka:** haz un `POST /pagos` y muestra los logs de ms-cuentas (saldo) y ms-clientes (notificación) + el tópico en Kafka-UI.
5. **BFF:** muestra la misma cuenta en los 3 canales (web completa vs móvil ligera vs cajero).

### 3:30–4:30 · Comparación con el sistema legacy
Usa la tabla del informe (sección 8). Destaca 3 mejoras:
- Escalabilidad horizontal por servicio vs vertical del monolito.
- Tolerancia a fallos con circuit breakers vs un fallo que tumbaba todo.
- Seguridad OAuth2 distribuida vs seguridad centralizada y limitada.

### 4:30–5:30 · Desafíos enfrentados y soluciones
- Datos legacy con anomalías → validación + Skip/Retry en Spring Batch.
- Seguridad sin acoplar credenciales → Authorization Server OAuth2 + Resource Servers.
- Arranque ordenado en Docker → healthchecks + depends_on + reintentos de Config.
- Comunicación desacoplada → Kafka pub/sub.

### 5:30–6:30 · Propuestas de mejora y cierre (webcam)
- Persistencia gestionada (RDS), secretos en Secrets Manager, orquestación con ECS/EKS, observabilidad (CloudWatch).
- Cierre: "Con esto el Banco XYZ queda con una base moderna, segura y escalable, lista para producción en la nube. Gracias."

---

## Checklist de grabación
- [ ] Webcam visible durante la intro y el cierre.
- [ ] Duración entre 5 y 7 minutos.
- [ ] Se muestran evidencias reales en pantalla (no solo diapositivas).
- [ ] Se abordan los 4 puntos (resumen, resultados+comparación, desafíos+soluciones, mejoras).
- [ ] Audio claro; formato MP4; subido por Kaltura.
