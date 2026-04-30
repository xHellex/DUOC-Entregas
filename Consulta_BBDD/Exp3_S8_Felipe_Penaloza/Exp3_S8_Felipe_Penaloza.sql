/* ==========================================================
   ACTIVIDAD SUMATIVA SEMANA 8: CONTROL DE ACCESO Y OPTIMIZACIÓN
   ========================================================== */

/* Configuración de sesión */
ALTER SESSION SET NLS_DATE_FORMAT='DD/MM/RRRR';


/* ==========================================================
   BLOQUE ADMIN: crear roles y usuarios (ejecutar como SYSTEM o ADMIN)
   ========================================================== */

-- Crear roles
CREATE ROLE PRY2205_ROL_D;
CREATE ROLE PRY2205_ROL_P;

-- Privilegios para roles
GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE INDEX, CREATE SYNONYM, CREATE SEQUENCE, CREATE TRIGGER TO PRY2205_ROL_D;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER TO PRY2205_ROL_P;

-- Crear usuarios
CREATE USER PRY2205_USER1 IDENTIFIED BY "Pry2024.User1" DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE TEMP QUOTA UNLIMITED ON USERS;
CREATE USER PRY2205_USER2 IDENTIFIED BY "Pry2024.User2" DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE TEMP QUOTA UNLIMITED ON USERS;

-- Asignar roles a usuarios
GRANT PRY2205_ROL_D TO PRY2205_USER1;
GRANT PRY2205_ROL_P TO PRY2205_USER2;

-- Conceder UNLIMITED TABLESPACE a usuarios (sólo a usuarios, no a roles)
GRANT UNLIMITED TABLESPACE TO PRY2205_USER1;
GRANT UNLIMITED TABLESPACE TO PRY2205_USER2;

-- NOTA: Si quieres crear SYNONYMS PÚBLICOS, el usuario que los crea necesita el privilegio CREATE PUBLIC SYNONYM.
-- Si prefieres, crea los sinónimos públicos con SYSTEM/ADMIN o crea synónimos privados como alternativa.


/* ==========================================================
   BLOQUE PRY2205_USER1 (Dueño del esquema) - sinónimos y permisos
   EJECUTAR con PRY2205_USER1 (o que SYSTEM cree los públicos si no tiene privilegio)
   ========================================================== */

-- Crear sinónimos públicos
CREATE OR REPLACE PUBLIC SYNONYM LIBRO FOR PRY2205_USER1.LIBRO;
CREATE OR REPLACE PUBLIC SYNONYM EJEMPLAR FOR PRY2205_USER1.EJEMPLAR;
CREATE OR REPLACE PUBLIC SYNONYM PRESTAMO FOR PRY2205_USER1.PRESTAMO;
CREATE OR REPLACE PUBLIC SYNONYM EMPLEADO FOR PRY2205_USER1.EMPLEADO;
CREATE OR REPLACE PUBLIC SYNONYM ALUMNO FOR PRY2205_USER1.ALUMNO;
CREATE OR REPLACE PUBLIC SYNONYM CARRERA FOR PRY2205_USER1.CARRERA;
CREATE OR REPLACE PUBLIC SYNONYM REBAJA_MULTA FOR PRY2205_USER1.REBAJA_MULTA;

-- Conceder SELECT sobre las tablas propietarias al rol del programador
GRANT SELECT ON PRY2205_USER1.LIBRO TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.EJEMPLAR TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.PRESTAMO TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.EMPLEADO TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.ALUMNO TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.CARRERA TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.REBAJA_MULTA TO PRY2205_ROL_P;

-- Crear sinónimos privados en el esquema PRY2205_USER1 para uso interno
CREATE OR REPLACE SYNONYM L_LIBRO FOR PRY2205_USER1.LIBRO;
CREATE OR REPLACE SYNONYM L_PRESTAMO FOR PRY2205_USER1.PRESTAMO;
CREATE OR REPLACE SYNONYM L_EJEMPLAR FOR PRY2205_USER1.EJEMPLAR;
CREATE OR REPLACE SYNONYM L_ALUMNO FOR PRY2205_USER1.ALUMNO;
CREATE OR REPLACE SYNONYM L_CARRERA FOR PRY2205_USER1.CARRERA;
CREATE OR REPLACE SYNONYM L_REBAJA FOR PRY2205_USER1.REBAJA_MULTA;


/* ==========================================================
   BLOQUE PRY2205_USER2 (Programador) - Caso 2: CONTROL_STOCK_LIBROS
   EJECUTAR con PRY2205_USER2
   ========================================================== */

-- Crear secuencia para correlativo
CREATE SEQUENCE SEQ_CONTROL_STOCK START WITH 1 INCREMENT BY 1 NOCACHE;

-- Crear tabla de reporte 
CREATE TABLE CONTROL_STOCK_LIBROS (
    CORRELATIVO             NUMBER(6) PRIMARY KEY,
    FECHA_PROCESO           VARCHAR2(7),
    ID_LIBRO                NUMBER(5),
    NOMBRE_LIBRO            VARCHAR2(70),
    CANTIDAD_TOTAL          NUMBER(3),
    EJEMPLARES_PRESTAMO     NUMBER(3),
    EJEMPLARES_DISPONIBLES  NUMBER(3),
    PORCENTAJE_PRESTAMO     VARCHAR2(10),
    STOCK_CRITICO           CHAR(1)
);

-- Insertar en CONTROL_STOCK_LIBROS con lógica paramétrica
INSERT INTO CONTROL_STOCK_LIBROS
SELECT 
    SEQ_CONTROL_STOCK.NEXTVAL,
    TO_CHAR(ADD_MONTHS(SYSDATE, -24), 'MM/YYYY') AS FECHA_PROCESO,
    l.libroid,
    INITCAP(l.titulo),
    COUNT(e.ejemplarid) AS CANTIDAD_TOTAL,
    -- Conteo condicional: solo préstamos gestionados por empleados 150,180,190 en el año
    NVL(SUM(CASE 
        WHEN p.empleadoid IN (150,180,190)
         AND EXTRACT(YEAR FROM p.fecha_entrega) = EXTRACT(YEAR FROM ADD_MONTHS(SYSDATE, -24))
        THEN 1 ELSE 0 END), 0) AS EJEMPLARES_PRESTAMO,
    -- Disponibles = Total - Prestados 
    (COUNT(e.ejemplarid) - NVL(SUM(CASE 
        WHEN p.empleadoid IN (150,180,190)
         AND EXTRACT(YEAR FROM p.fecha_entrega) = EXTRACT(YEAR FROM ADD_MONTHS(SYSDATE, -24))
        THEN 1 ELSE 0 END), 0)) AS EJEMPLARES_DISPONIBLES,
    -- Porcentaje formateado 
    CASE WHEN COUNT(e.ejemplarid) = 0 THEN '0%'
         ELSE TO_CHAR(ROUND(
            (NVL(SUM(CASE 
                WHEN p.empleadoid IN (150,180,190)
                 AND EXTRACT(YEAR FROM p.fecha_entrega) = EXTRACT(YEAR FROM ADD_MONTHS(SYSDATE, -24))
                THEN 1 ELSE 0 END),0) / COUNT(e.ejemplarid)) * 100
         ), '990') || '%' END AS PORCENTAJE_PRESTAMO,
    -- Stock critico
    CASE 
        WHEN (COUNT(e.ejemplarid) - NVL(SUM(CASE 
            WHEN p.empleadoid IN (150,180,190)
             AND EXTRACT(YEAR FROM p.fecha_entrega) = EXTRACT(YEAR FROM ADD_MONTHS(SYSDATE, -24))
            THEN 1 ELSE 0 END),0)) > 2 THEN 'S' ELSE 'N' 
    END AS STOCK_CRITICO
FROM 
    LIBRO l
    JOIN EJEMPLAR e ON l.libroid = e.libroid
    LEFT JOIN PRESTAMO p ON e.ejemplarid = p.ejemplarid AND e.libroid = p.libroid
GROUP BY 
    l.libroid, l.titulo
ORDER BY 
    l.libroid;

COMMIT;

-- Verificación
SELECT * FROM CONTROL_STOCK_LIBROS ORDER BY ID_LIBRO;


 /*************************************************************************
   BLOQUE PRY2205_USER1 (Dueño) - Caso 3: Vista de multas y optimización
   EJECUTAR con PRY2205_USER1
 *************************************************************************/

-- Crear vista que muestra multas por préstamos atrasados (año = actual - 2)
CREATE OR REPLACE VIEW VW_DETALLE_MULTAS AS
SELECT 
    p.prestamoid AS ID_PRESTAMO,
    INITCAP(a.nombre || ' ' || a.appaterno || ' ' || a.apmaterno) AS ALUMNO,
    UPPER(c.descripcion) AS CARRERA,
    l.codigolibro AS CODIGO_LIBRO,
    TO_CHAR(l.precio, '$999G999') AS PRECIO_LIBRO,
    TO_CHAR(p.fecha_entrega, 'DD-MM-YYYY') AS FECHA_PRESTAMO,
    TO_CHAR(p.fecha_devolucion, 'DD-MM-YYYY') AS FECHA_SOLICITADA,
    TO_CHAR(p.fecha_devolucion_real, 'DD-MM-YYYY') AS FECHA_DEVOLUCION,
    (p.fecha_devolucion_real - p.fecha_devolucion) AS DIAS_ATRASO,
    -- Multa base: 3% del valor del libro por cada día de atraso
    TO_CHAR(ROUND((l.precio * 0.03) * (p.fecha_devolucion_real - p.fecha_devolucion)), '$999G999') AS VALOR_MULTA,
    -- Rebaja según convenio de carrera (si existe)
    TO_CHAR(ROUND(NVL(((l.precio * 0.03) * (p.fecha_devolucion_real - p.fecha_devolucion)) * (r.porcentaje_rebaja / 100), 0)), '$999G999') AS REBAJA,
    -- Multa final (multabase - rebaja)
    TO_CHAR(ROUND(((l.precio * 0.03) * (p.fecha_devolucion_real - p.fecha_devolucion)) - NVL(((l.precio * 0.03) * (p.fecha_devolucion_real - p.fecha_devolucion)) * (r.porcentaje_rebaja / 100), 0)), '$999G999') AS MULTA_FINAL
FROM 
    PRESTAMO p
    JOIN EJEMPLAR e ON p.ejemplarid = e.ejemplarid AND p.libroid = e.libroid
    JOIN LIBRO l ON e.libroid = l.libroid
    JOIN ALUMNO a ON p.alumnoid = a.alumnoid
    JOIN CARRERA c ON a.carreraid = c.carreraid
    LEFT JOIN REBAJA_MULTA r ON c.carreraid = r.carreraid
WHERE 
    EXTRACT(YEAR FROM p.fecha_devolucion_real) = (EXTRACT(YEAR FROM SYSDATE) - 2)
    AND p.fecha_devolucion_real > p.fecha_devolucion
ORDER BY 
    p.fecha_devolucion_real DESC;


-- Índice en campo fecha_devolucion_real
CREATE INDEX IDX_PRESTAMO_FECHA_DEV_REAL ON PRESTAMO(fecha_devolucion_real);

-- Índice compuesto para acelerar joins
CREATE INDEX IDX_PRESTAMO_JOIN ON PRESTAMO(alumnoid, ejemplarid, libroid);

-- Índice en tabla de rebajas por carrera
CREATE INDEX IDX_REBAJA_CARRERA ON REBAJA_MULTA(carreraid);

-- Verificación de la vista
SELECT * FROM VW_DETALLE_MULTAS WHERE ROWNUM <= 50;
