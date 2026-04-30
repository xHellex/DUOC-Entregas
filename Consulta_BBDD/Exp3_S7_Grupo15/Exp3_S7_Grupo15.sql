-- Caso 1 --

-- Creación de sinónimos para la vista
CREATE OR REPLACE SYNONYM S_BONO_ESC FOR BONO_ESCOLAR;
CREATE OR REPLACE SYNONYM S_ASIG_FAM FOR ASIGNACION_FAMILIAR;

-- Creación de la Vista
CREATE OR REPLACE VIEW V_AUMENTOS_ESTUDIOS AS
SELECT 
    t.numrut || '-' || t.dvrut AS RUT_TRABAJADOR,
    INITCAP(t.nombre || ' ' || t.appaterno || ' ' || t.apmaterno) AS TRABAJADOR,
    be.descrip AS DESCRIP,
    be.porc_bono || '%' AS PCT_ESTUDIOS, -- Formato porcentaje
    TO_CHAR(t.sueldo_base, '$99G999G999') AS SUELDO_ACTUAL,
    TO_CHAR(ROUND(t.sueldo_base * (be.porc_bono / 100)), '$99G999G999') AS AUMENTO,
    TO_CHAR(ROUND(t.sueldo_base * (1 + (be.porc_bono / 100))), '$99G999G999') AS SUELDO_AUMENTADO
FROM S_TRABAJADOR t
    JOIN S_BONO_ESC be ON t.id_escolaridad_t = be.id_escolar
    JOIN tipo_trabajador tt ON t.id_categoria_t = tt.id_categoria
    JOIN ( -- Subconsulta requerida para contar cargas
        SELECT numrut_t, COUNT(*) as cant_cargas
        FROM S_ASIG_FAM
        GROUP BY numrut_t
    ) af ON t.numrut = af.numrut_t
WHERE 
    UPPER(tt.desc_categoria) = 'CAJERO'
    AND af.cant_cargas BETWEEN 1 AND 2 -- Filtro de 1 o 2 cargas
ORDER BY be.porc_bono ASC, TRABAJADOR ASC;

-- Verificación
SELECT * FROM V_AUMENTOS_ESTUDIOS;


-- Caso 2 --

-- 1. Índice B-Tree para búsqueda exacta
CREATE INDEX idx_trab_apmaterno ON trabajador(apmaterno);

-- 2. Índice Basado en Función para búsqueda con UPPER
CREATE INDEX idx_trab_apmat_upper ON trabajador(UPPER(apmaterno));

-- Queries para probar el plan de ejecución (F10 en SQL Developer)
SELECT numrut, appaterno, apmaterno, nombre, sueldo_base
FROM trabajador
WHERE apmaterno = 'CASTILLO';

SELECT numrut, appaterno, apmaterno, nombre, sueldo_base
FROM trabajador
WHERE UPPER(apmaterno) = 'CASTILLO';