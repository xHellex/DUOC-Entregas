# Migración de Procesos Batch - Banco XYZ (Semana 2)

**Desarrollo Backend III (PBY2203) · Experiencia 1 · Semana 2**
Grupo 3 — Actividad formativa: *Configurando jobs y steps en Spring Batch*

**Repositorio:** https://github.com/xHellex/DUOC-Entregas/tree/main/Backend/Exp1_S2

---

## 1. Objetivo del proyecto

Este proyecto es la continuidad de la actividad de la semana 1. Sobre la base de los tres procesos batch del Banco XYZ (transacciones diarias, intereses mensuales y estados de cuenta anuales), la semana 2 incorpora **técnicas avanzadas de configuración**: escalado y procesamiento paralelo, políticas personalizadas de tolerancia a fallos, políticas de finalización y re-ejecución, optimización de recursos, manejo de errores con listeners, y técnicas de logs.

## 2. Técnicas avanzadas implementadas (Semana 2)

### 2.1 Escalado y procesamiento paralelo

Cada Step de procesamiento se ejecuta en paralelo mediante un `ThreadPoolTaskExecutor` configurado con **3 hilos** y un **tamaño de chunk de 5**, según lo exigido por la actividad. Esto permite procesar los registros de forma simultánea, reduciendo el tiempo de ejecución.

- Clase: `advanced/BatchTaskExecutorConfig.java`
- Los readers se envuelven en `SynchronizedItemStreamReader` para garantizar la seguridad entre hilos (los `FlatFileItemReader` no son thread-safe por sí solos).

### 2.2 Optimización de recursos del sistema

El `TaskExecutor` limita el consumo de recursos para evitar saturar el sistema:

- `corePoolSize = 3` y `maxPoolSize = 3`: se mantienen exactamente 3 hilos, evitando el costo de crear y destruir hilos.
- `queueCapacity = 25`: las tareas en espera se encolan de forma acotada, controlando el uso de memoria.

### 2.3 Políticas personalizadas y tolerancia a fallos

- `advanced/CustomSkipPolicy.java`: política de omisión (`SkipPolicy`) personalizada que decide, por cada excepción, si la fila se omite o si el error detiene el proceso. Omite `FlatFileParseException` (líneas corruptas) e `InvalidDataException` (violaciones de reglas de negocio) hasta un límite.
- Cada Step activa `faultTolerant()` con esta política.

### 2.4 Políticas de finalización y re-ejecución

- `advanced/CustomDecider.java`: un `JobExecutionDecider` evalúa el resultado del Step. Si no hubo fallos, el Job finaliza (`COMPLETED`); si hubo fallos, devuelve el estado `RETRY` y el flujo vuelve a ejecutar el Step. Esto aporta resiliencia ante fallos recuperables.

### 2.5 Manejo de errores con listeners

- `advanced/RegistroSkipListener.java`: un `SkipListener` que registra cada elemento omitido, tanto en el log como en un archivo de errores CSV (`salida/errores_<job>.csv`), indicando la fase (lectura, proceso o escritura) y el motivo.
- `advanced/JobCompletionListener.java`: un `JobExecutionListener` que registra el inicio y el fin de cada Job con un resumen del estado y la duración.

### 2.6 Técnicas de logs

El patrón de logging incluye el nombre del hilo `[%thread]`, de modo que en la consola se observan los tres hilos de ejecución paralela (`Batch-Thread-1`, `Batch-Thread-2`, `Batch-Thread-3`) procesando simultáneamente. Los logs con prefijos `[SKIP]`, `[DECIDER]`, `[JOB]`, `[RESUMEN]` e `[INFORME]` permiten trazar y depurar la ejecución.

## 3. Arquitectura

Cada proceso es un **Job**. Los Steps de procesamiento siguen el patrón chunk con paralelismo:

```
Step de procesamiento (paralelo, 3 hilos, chunk=5):
  SynchronizedItemStreamReader → ItemProcessor → RepositoryItemWriter
       (lee el CSV)               (valida, lanza    (persiste en BD)
                                   InvalidDataException
                                   ante anomalías)
  + CustomSkipPolicy (omite errores)
  + RegistroSkipListener (registra omitidos en CSV)

Decider (COMPLETED → continúa · RETRY → reintenta el Step)

Step de consolidación (Jobs 1 y 3):
  Tasklet → genera resumen/informe (BD + CSV)
```

## 4. Stack tecnológico

| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 3.4.1 |
| Lenguaje | Java 17 |
| Batch | Spring Batch (paralelismo, skip/retry, decider, listeners) |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL 8 (perfil `mysql`) · H2 en memoria (perfil `h2`) |
| Build | Maven |

## 5. Estructura del código

```
Exp1_S2_Grupo3/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/bancoxyz/batch/
    │   ├── MigracionBatchApplication.java
    │   ├── BatchRunner.java                    Lanza los 3 jobs en orden
    │   ├── advanced/                           ★ Componentes avanzados S2
    │   │   ├── BatchTaskExecutorConfig.java    TaskExecutor: 3 hilos
    │   │   ├── CustomSkipPolicy.java           Política de omisión
    │   │   ├── CustomDecider.java              Finalización / re-ejecución
    │   │   ├── RegistroSkipListener.java       Listener de errores → CSV
    │   │   ├── JobCompletionListener.java      Listener de inicio/fin de Job
    │   │   └── InvalidDataException.java        Excepción de dominio
    │   ├── config/                             Las 3 configuraciones de Job
    │   ├── model/                              Entidades y DTOs
    │   ├── processor/                          Validaciones (lanzan excepción)
    │   ├── tasklet/                            Resumen e informe
    │   ├── repository/                         Repositorios Spring Data
    │   ├── listener/ResumenJobListener         Resumen de steps
    │   └── util/FechaLegacyParser              Parseo de fechas
    └── resources/
        ├── application.properties              Config común + perfil + logs
        ├── application-h2.properties           Perfil H2 (desarrollo)
        ├── application-mysql.properties        Perfil MySQL (producción)
        └── data/semana_1  ·  data/semana_3
```

## 6. Instrucciones de ejecución

### Requisitos previos
- JDK 17 o superior
- Maven 3.9+
- Para el perfil MySQL: una instancia MySQL 8 con la base `bancoxyz`

### Ejecución con MySQL (base productiva)

```sql
CREATE DATABASE bancoxyz CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Ajusta usuario y clave en `application-mysql.properties` y ejecuta:

```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Ejecución rápida con H2 (desarrollo)

```bash
mvn clean spring-boot:run
```

Consola H2: `http://localhost:8080/h2-console` — JDBC `jdbc:h2:mem:bancoxyz`, usuario `sa`, sin contraseña.

### Cambiar el volumen de datos

```properties
app.data.path=data/semana_1   # 10 filas (desarrollo)
app.data.path=data/semana_3   # 1000 filas (demostrar escalado y errores)
```

### Salidas generadas

- Tablas: `transaccion`, `cuenta_interes`, `cuenta_anual`, `resumen_transacciones`, `estado_cuenta_anual`.
- Archivos CSV en `salida/`: resumen, informe anual y **archivos de errores** (`errores_transacciones.csv`, `errores_intereses.csv`, `errores_cuentas_anuales.csv`).

## 7. Evidencia de ejecución

Para las evidencias se recomienda ejecutar con el **perfil MySQL** y el conjunto **semana_3**, capturando:

1. La consola mostrando los tres hilos `Batch-Thread-*` procesando en paralelo.
2. Los logs `[SKIP]`, `[DECIDER]` y `[JOB]` que trazan el manejo de errores y el flujo.
3. Las tablas pobladas en MySQL Workbench.
4. Los archivos de errores CSV generados en `salida/`.

## 8. Integrantes

Grupo 3 — Desarrollo Backend III (PBY2203)
