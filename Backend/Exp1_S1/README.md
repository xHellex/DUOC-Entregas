# Migración de Procesos Batch - Banco XYZ

**Desarrollo Backend III (PBY2203) · Experiencia 1 · Semana 1**
Grupo 3 — Actividad formativa: *Analizando la arquitectura batch para procesar datos*

---

## 1. Objetivo del proyecto

Modernizar el sistema *legacy* del Banco XYZ recreando con **Spring Batch** tres procesos por lotes que antes vivían en programas COBOL / Shell:

1. **Reporte de Transacciones Diarias** — procesa las transacciones, detecta anomalías, persiste las válidas y **genera un resumen** consolidado (a BD y CSV).
2. **Cálculo de Intereses Mensuales** — aplica la tasa de interés según el tipo de cuenta, calcula el saldo final y lo actualiza en la base de datos.
3. **Generación de Estados de Cuenta Anuales** — compila y normaliza las operaciones anuales y **genera un informe detallado por cuenta** para auditorías (a BD y CSV).

Los datos de origen provienen de [bank_legacy_data](https://github.com/KariVillagran/bank_legacy_data) y contienen, de forma deliberada, anomalías típicas de un entorno legacy: montos negativos o cero, fechas mal formateadas, campos vacíos, registros duplicados y valores fuera de rango.

## 2. Arquitectura

Cada proceso es un **Job**. Los Jobs 1 y 3 se componen de **dos Steps**; el Job 2 de uno.

```
Step de procesamiento:
  FlatFileItemReader  →  ItemProcessor  →  RepositoryItemWriter
     (lee el CSV)        (valida/transforma)   (persiste en BD)

Step de consolidación (Jobs 1 y 3):
  Tasklet  →  agrega los datos persistidos  →  escribe resumen/informe (BD + CSV)
```

- **ItemReader**: `FlatFileItemReader` lee cada CSV y mapea las columnas a un DTO de entrada (`*Input`) con todos los campos como `String`, porque el archivo legacy contiene valores que no pueden convertirse directamente a tipos fuertes.
- **ItemProcessor**: aplica las reglas de negocio. Si una fila es anómala, devuelve `null` y Spring Batch la descarta (se contabiliza en `filterCount`). Si es válida, la transforma en la entidad final.
- **ItemWriter**: `RepositoryItemWriter` persiste las entidades válidas usando el repositorio Spring Data (`save`).
- **Tasklet de consolidación**: en los Jobs 1 y 3, un segundo Step lee lo persistido, lo agrega y genera el resumen/informe en la base de datos y en un archivo CSV bajo `salida/`.
- **JobRepository**: Spring Batch persiste los metadatos de cada ejecución en las tablas `BATCH_*`.

## 3. Manejo de errores (skip + retry)

Cada Step de procesamiento es `faultTolerant()` con dos políticas complementarias:

- **skip** (`skipLimit(1000)`, `skip(Exception.class)`): si una línea del CSV lanza una excepción de parseo, se omite en lugar de abortar el job. Además, las filas anómalas detectadas por reglas de negocio se descartan en el processor devolviendo `null`.
- **retry** (`retryLimit(3)`, `retry(DataAccessException.class)`): reintenta hasta tres veces ante fallos transitorios de acceso a datos, como un bloqueo temporal de la base de datos.

| Job | Anomalía | Tratamiento |
|---|---|---|
| Transacciones | monto vacío, no numérico, ≤ 0 | se descarta la fila |
| Transacciones | tipo distinto de débito/crédito (`invalid`, `desconocido`) | se descarta la fila |
| Transacciones | fecha vacía o inválida (`2024-13-01`, `2024/01/04`) | se normaliza o se descarta |
| Intereses | saldo vacío o ≤ 0 | se descarta la fila |
| Intereses | edad fuera de rango [18, 75] | se descarta la fila |
| Intereses | tipo desconocido | se descarta la fila |
| Intereses | cuenta duplicada (mismo `cuenta_id`) | sobrescribe (idempotente por `@Id`) |
| Cuentas anuales | fecha inválida | se descarta la fila |
| Cuentas anuales | monto vacío/no numérico | se descarta la fila |
| Cuentas anuales | descripción faltante | se completa con "Sin descripcion" |

## 4. Stack tecnológico

| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 3.4.1 |
| Lenguaje | Java 17 |
| Batch | Spring Batch |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL 8 (perfil `mysql`) · H2 en memoria (perfil `h2`) |
| Build | Maven |

## 5. Estructura del código

```
Exp1_S1_Grupo3/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/bancoxyz/batch/
    │   ├── MigracionBatchApplication.java      Clase principal
    │   ├── BatchRunner.java                    Lanza los 3 jobs en orden
    │   ├── config/
    │   │   ├── TransaccionesBatchConfig.java   Job 1: 2 steps (proceso + resumen)
    │   │   ├── InteresesBatchConfig.java       Job 2: 1 step
    │   │   └── CuentasAnualesBatchConfig.java  Job 3: 2 steps (proceso + informe)
    │   ├── model/
    │   │   ├── Transaccion / TransaccionInput
    │   │   ├── CuentaInteres / InteresInput
    │   │   ├── CuentaAnual / CuentaAnualInput
    │   │   ├── ResumenTransacciones            Salida agregada del Job 1
    │   │   └── EstadoCuentaAnual               Salida agregada del Job 3
    │   ├── processor/                          Reglas de anomalías por job
    │   ├── tasklet/
    │   │   ├── ResumenTransaccionesTasklet     Genera resumen (BD + CSV)
    │   │   └── InformeAnualTasklet             Genera informe (BD + CSV)
    │   ├── repository/                         Repositorios Spring Data
    │   ├── listener/ResumenJobListener         Imprime el resumen de cada job
    │   └── util/FechaLegacyParser              Parseo de fechas multi-formato
    └── resources/
        ├── application.properties              Config común + perfil activo
        ├── application-h2.properties           Perfil H2 (desarrollo)
        ├── application-mysql.properties        Perfil MySQL (producción)
        └── data/
            ├── semana_1/   (datos limpios, para desarrollar)
            └── semana_3/   (1000 filas con todas las anomalías)
```

## 6. Instrucciones de ejecución

### Requisitos previos
- JDK 17 o superior
- Maven 3.9+ (o el wrapper `./mvnw`)
- Para el perfil MySQL: una instancia MySQL 8 en ejecución

### Opción A — Ejecución rápida con H2 (sin instalar nada)

El perfil `h2` viene activo por defecto en `application.properties`:

```bash
git clone https://github.com/<tu-usuario>/Exp1_S1_Grupo3.git
cd Exp1_S1_Grupo3
mvn clean spring-boot:run
```

Consola H2: `http://localhost:8080/h2-console` — JDBC `jdbc:h2:mem:bancoxyz`, usuario `sa`, sin contraseña.

### Opción B — Ejecución con MySQL

1. Crea la base de datos:

```sql
CREATE DATABASE bancoxyz CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Ajusta usuario y clave en `src/main/resources/application-mysql.properties`.

3. Activa el perfil `mysql` de una de estas dos formas:

```bash
# opción 1: por línea de comando
mvn spring-boot:run -Dspring-boot.run.profiles=mysql

# opción 2: cambia en application.properties
#   spring.profiles.active=mysql
```

### Cambiar el set de datos

Para demostrar el manejo de errores a escala, edita `application.properties`:

```properties
app.data.path=data/semana_1   # datos limpios (10 filas)
app.data.path=data/semana_3   # 1000 filas con todas las anomalías
```

### Salidas generadas

Al ejecutar, además de persistir en la base de datos, los Jobs 1 y 3 escriben archivos CSV en la carpeta `salida/`:

- `salida/resumen_transacciones_<timestamp>.csv`
- `salida/informe_anual_<timestamp>.csv`

Las tablas de salida para consultar:

```sql
SELECT * FROM transaccion;
SELECT * FROM cuenta_interes;
SELECT * FROM cuenta_anual;
SELECT * FROM resumen_transacciones;   -- resumen del Job 1
SELECT * FROM estado_cuenta_anual;     -- informe del Job 3
```

## 7. Resultados esperados (referencia)

| Set | Job | Válidas | Anomalías |
|---|---|---|---|
| semana_1 | Transacciones | 8 | 2 |
| semana_1 | Intereses | 6 | 2 |
| semana_1 | Cuentas anuales | 9 | 0 |
| semana_3 | Transacciones | 401 | 599 |
| semana_3 | Intereses | 329 | 671 |
| semana_3 | Cuentas anuales | 952 | 48 |

En `salida/` encontrarás el resumen agregado (débitos/créditos y montos) y el informe anual con ingresos, egresos y saldo neto por cuenta.

## 8. Integrantes

Grupo 3 — Desarrollo Backend III (PBY2203)
