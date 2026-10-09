# EFT · Parte 1 — Migración de Procesos Batch (Spring Batch)

Módulo `batch-service`. Reescribe en Spring Batch los 3 procesos legacy del Banco XYZ usando el dataset oficial **fin_legacy_data**.

## Los 3 jobs

| Job | Proceso legacy | Archivo de entrada | Qué hace |
|-----|----------------|--------------------|----------|
| 1 | Reporte de Transacciones Diarias | `movimientos_financieros_diarios.csv` | Valida y normaliza movimientos; detecta anomalías (monto ≤ 0, tipo inválido, fecha mal formada) |
| 2 | Cálculo de Intereses | `intereses_trimestrales.csv` | Calcula interés y saldo final por cuenta; descarta saldo faltante, edad fuera de [18,75], tipo desconocido |
| 3 | Estados de Cuenta Anuales | `estados_financieros_anuales.csv` | Compila operaciones anuales; conserva montos negativos (retiros), completa descripciones vacías |

Los 3 se lanzan en orden desde `BatchRunner`.

## Cómo cumple la pauta (criterio 3 — 15 pts)

- **3 procesos batch**: `TransaccionesBatchConfig`, `InteresesBatchConfig`, `CuentasAnualesBatchConfig`.
- **Manejo avanzado de errores**: cada anomalía lanza `InvalidDataException` → `CustomSkipPolicy` la omite y `RegistroSkipListener` la registra (log + archivo de errores). No detiene el job.
- **Reintentos**: política de retry ante fallos temporales (p. ej. caída de BD).
- **Paralelismo / escalabilidad**: `RangoPartitioner` + `TaskExecutorPartitionHandler` dividen cada archivo en `app.grid.size` particiones ejecutadas en paralelo (verás `[Batch-Thread-1/2/3]` en consola).
- **Reejecución**: cada corrida agrega un `timestamp` como parámetro, por lo que el job se puede re-lanzar sin chocar con una instancia COMPLETED previa.

## Requisitos

- Java 17 (tu JDK 22 también sirve), Maven.

## Ejecutar

```bash
cd batch-service
mvn spring-boot:run
```

Por defecto corre con perfil `h2` (BD en memoria) sobre el dataset de volumen (1000 filas/archivo).

### Ver los datos procesados (consola H2)

Mientras la app corre: http://localhost:8080/h2-console
JDBC URL: `jdbc:h2:mem:bancoxyz` · user `sa` · sin clave.
Tablas: `TRANSACCION`, `CUENTA_INTERES`, `ESTADO_CUENTA_ANUAL` + las `BATCH_*` de metadatos.

### Probar escalabilidad (comparar configuraciones)

Cambia el número de particiones/hilos y compara tiempos:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--app.grid.size=1   # secuencial
mvn spring-boot:run -Dspring-boot.run.arguments=--app.grid.size=4   # 4 hilos
```

## Resultados esperados con el dataset oficial (semana_3, 1000 filas c/u)

| Job | Válidos | Omitidos (skip) |
|-----|---------|-----------------|
| 1 — Transacciones | ~392 | ~608 (tipo 266, monto 313, fecha 29) |
| 2 — Intereses | ~340 | ~660 (edad 286, saldo 204, tipo 170) |
| 3 — Estados anuales | ~952 | ~48 (monto no numérico); 232 negativos conservados, 184 descripciones completadas |

## Evidencia a capturar para el informe/video

1. Consola con el arranque de los 3 jobs y las líneas `[Batch-Thread-N]` (paralelismo).
2. Resumen de cada job (leídos / escritos / omitidos) en el log.
3. Consola H2 con filas en las 3 tablas.
4. Una corrida con `grid.size` distinto para comparar tiempos (escalabilidad).
