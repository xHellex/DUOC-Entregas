# Migración de Procesos Batch - Banco XYZ (Semana 3)

**Desarrollo Backend III (PBY2203) · Experiencia 1 · Semana 3**
Actividad sumativa individual: *Optimizando procesos batch para mejorar la resiliencia de procesos*

**Estudiante:** Felipe Penaloza
**Repositorio:** https://github.com/xHellex/DUOC-Entregas/tree/main/Backend/Exp1_S3

---

## 1. Objetivo del proyecto

Continuidad del proyecto de las semanas 1 y 2. Sobre los tres procesos batch del Banco XYZ (transacciones diarias, intereses mensuales y estados de cuenta anuales), la semana 3 incorpora **escalado por particionamiento** para procesar los datos en paralelo, junto con la comparación de configuraciones para hallar la óptima, manteniendo las políticas de tolerancia a fallos y reintento que garantizan la resiliencia del sistema.

## 2. Datos de entrada

Los datos provienen del repositorio oficial [bank_legacy_data](https://github.com/KariVillagran/bank_legacy_data) y están ubicados directamente en `src/main/resources/data/`, tal como en el repositorio de origen:

- `data/transacciones.csv`, `data/intereses.csv`, `data/cuentas_anuales.csv` — **conjunto oficial**, usado por configuración por defecto.
- `data/volumen_grande/` (1000 filas) y `data/volumen_xl/` (100 000 filas) — conjuntos sintéticos generados con `scripts/generar_datos.py`, mismo formato y tipos de anomalía que el oficial, para las **pruebas de rendimiento y comparación de configuraciones** de particionamiento.

El conjunto activo se controla con `app.data.path` en `application.properties`. Para regenerar los conjuntos de volumen:

```bash
python scripts/generar_datos.py 100000 src/main/resources/data/volumen_xl
```

## 3. Escalado por particionamiento (técnica de la S3)

Cada Job procesa su Step principal como un **PartitionStep** que divide el trabajo en particiones ejecutadas en paralelo:

```
PartitionStep (paso principal)
   └─ RangoPartitioner divide los datos en N rangos (start–end)
        └─ TaskExecutorPartitionHandler (gridSize particiones, en paralelo)
             ├─ minionStep · partición 0 · hilo Batch-Part-1
             ├─ minionStep · partición 1 · hilo Batch-Part-2
             └─ minionStep · partición 2 · hilo Batch-Part-3
        └─ agrega los resultados
```

- `advanced/RangoPartitioner.java`: implementa `Partitioner`. Divide el total de registros en `gridSize` particiones, asignando a cada una un rango `start`–`end` en su `ExecutionContext`.
- Reader `@StepScope`: cada partición tiene su propio `FlatFileItemReader` que lee las claves `start`/`end` y procesa solo su rango (`linesToSkip` + `maxItemCount`), evitando solapamientos.
- `TaskExecutorPartitionHandler` con `setGridSize(N)`: ejecuta las particiones en paralelo sobre el `TaskExecutor`.
- `minionStep`: el paso worker que procesa una partición, con toda la tolerancia a fallos (skip, retry, listener).

El número de particiones es configurable con `app.grid.size`, lo que permite **comparar configuraciones** y encontrar la óptima.

## 4. Tolerancia a fallos y reintento

Cada `minionStep` conserva las políticas de resiliencia:

- **skip** (`CustomSkipPolicy`): omite `FlatFileParseException` e `InvalidDataException` hasta un límite configurable (`app.skip.limit`, por defecto 100 000), registrando cada omisión. El límite es un parámetro más de la tolerancia a fallos: se ajusta según el volumen de datos esperado.
- **retry** (`retryLimit(3)`, `retry(DataAccessException.class)`): reintenta fallos transitorios de acceso a datos.
- **SkipListener** (`RegistroSkipListener`): escribe cada omisión a `salida/errores_<job>.csv` con fase y motivo.

Al estar dentro de cada partición, un fallo en una partición se maneja de forma aislada sin afectar a las demás.

### Reejecución de jobs (idempotencia)

Cada Job arranca con un **step de limpieza** (`LimpiezaTablasTasklet`) que vacía sus tablas destino antes de procesar. Así el Job es **idempotente**: se puede re-ejecutar sin acumular filas de corridas anteriores ni distorsionar los conteos del resumen (`total_validas` vs `total_leidas`). Junto con el parámetro `timestamp` único por corrida del `BatchRunner`, esto cubre la política de repetición y reejecución de jobs.

## 5. Stack tecnológico

| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 3.4.1 |
| Lenguaje | Java 17 |
| Batch | Spring Batch (particionamiento, skip/retry, listeners) |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL 8 (perfil `mysql`) · H2 en memoria (perfil `h2`) |
| Build | Maven |

## 6. Estructura del código

```
Exp1_S3_Felipe_Penaloza/
├── scripts/generar_datos.py                  Generador de datasets de volumen
└── src/main/
    ├── java/com/bancoxyz/batch/
    │   ├── advanced/
    │   │   ├── RangoPartitioner.java          ★ Particionador (S3)
    │   │   ├── BatchTaskExecutorConfig.java    TaskExecutor (pool = gridSize)
    │   │   ├── CustomSkipPolicy.java           Política de omisión
    │   │   ├── RegistroSkipListener.java       Listener de errores → CSV
    │   │   ├── JobCompletionListener.java      Listener de inicio/fin
    │   │   └── InvalidDataException.java        Excepción de dominio
    │   ├── config/                             Las 3 configs con particionamiento
    │   ├── model/ · processor/ · tasklet/ · repository/ · util/
    │   └── util/ContadorRegistros.java         Cuenta registros para particionar
    └── resources/
        ├── application.properties              Config + app.grid.size + app.skip.limit
        ├── application-h2.properties · application-mysql.properties
        └── data/                               ★ Datos oficiales + volumen_grande/ + volumen_xl/
```

## 7. Instrucciones de ejecución

### Con MySQL (base productiva)

```sql
CREATE DATABASE bancoxyz CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
# PowerShell (comillas obligatorias en el -D)
mvn clean spring-boot:run "-Dspring-boot.run.profiles=mysql"

# bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Con H2 (desarrollo)

```bash
mvn clean spring-boot:run
```

### Comparar configuraciones de escalado

Para encontrar la configuración óptima, ejecutar variando `app.grid.size` sobre un conjunto de volumen:

```properties
app.data.path=data/volumen_xl   # 100 000 filas por archivo
app.grid.size=1                  # línea base (una sola partición, sin paralelismo real)
app.grid.size=2 / 3 / 4          # 2, 3, 4 particiones en paralelo
```

Cada ejecución reporta la duración de cada Job en el log (`[JOB] Resumen -> ... duracion aprox: N ms`), lo que permite construir la tabla comparativa. El script `scripts/bench.sh` automatiza la comparación.

## 8. Evidencia de ejecución

La carpeta `evidencia/` contiene las capturas de la ejecución con perfil MySQL sobre los datos oficiales:

1. Consola con las particiones (`[PARTICION] particion0 -> registros 0 a 3`) y los hilos `Batch-Part-1/2/3` procesando en paralelo (`transaccionMinionStep:particion0/1/2`, etc.).
2. Los tres Jobs finalizando en `COMPLETED`.
3. La tabla `batch_step_execution` en MySQL Workbench, donde cada `*MinionStep:particionN` aparece como un step de ejecución independiente.
4. Las tablas de dominio pobladas (`transaccion`, `cuenta_interes`, `estado_cuenta_anual`, `resumen_transacciones`).

### Resultado de la comparación de configuraciones (criterio 4)

Dataset `volumen_xl` (100 000 filas/archivo), máquina de 12 núcleos, perfil H2, mejor de 2 repeticiones:

| `app.grid.size` | Transacciones | Intereses | Cuentas anuales | **Total** | Speedup |
|:-:|:-:|:-:|:-:|:-:|:-:|
| 1 (base) | 16 800 ms | 17 119 ms | 16 810 ms | **50 729 ms** | 1,00× |
| 2 | 9 031 ms | 8 684 ms | 8 736 ms | **26 451 ms** | 1,92× |
| 3 | 7 000 ms | 6 692 ms | 6 515 ms | **20 207 ms** | 2,51× |
| 4 | 5 827 ms | 5 231 ms | 5 666 ms | **16 724 ms** | 3,03× |

El mayor salto ocurre de 1→2 particiones (speedup casi lineal). A partir de 3 la eficiencia por partición decae (0,96 → 0,84 → 0,76): aparece el costo de coordinar hilos y la contención en la escritura. **Configuración óptima: `app.grid.size=3`** (codo de la curva; equilibrio entre speedup y eficiencia), que es el valor por defecto del proyecto.

## 9. Datos

Este proyecto utiliza los archivos oficiales del Banco XYZ ubicados en `data/`, más un conjunto ampliado en `data/volumen_grande/` para demostrar el escalado con volúmenes mayores.
