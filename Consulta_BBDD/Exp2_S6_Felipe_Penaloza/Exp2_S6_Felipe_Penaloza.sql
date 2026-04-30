-- CASO 1: Profesionales versátiles (Banca y Retail)
SELECT 
    p.id_profesional AS "ID",
    INITCAP(p.appaterno || ' ' || p.apmaterno || ' ' || p.nombre) AS "PROFESIONAL",
    -- Cálculos Sector Banca (Cod 3)
    COUNT(CASE WHEN s.cod_sector = 3 THEN 1 END) AS "NRO ASESORIA BANCA",
    TO_CHAR(SUM(CASE WHEN s.cod_sector = 3 THEN a.honorario ELSE 0 END), '$999G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "MONTO_TOTAL_BANCA",
    -- Cálculos Sector Retail (Cod 4)
    COUNT(CASE WHEN s.cod_sector = 4 THEN 1 END) AS "NRO ASESORIA RETAIL",
    TO_CHAR(SUM(CASE WHEN s.cod_sector = 4 THEN a.honorario ELSE 0 END), '$999G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "MONTO_TOTAL_RETAIL",
    -- Totales Generales
    COUNT(a.cod_empresa) AS "TOTAL ASESORIAS",
    TO_CHAR(SUM(a.honorario), '$999G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "TOTAL HONORARIOS"
FROM profesional p
JOIN asesoria a ON p.id_profesional = a.id_profesional
JOIN empresa e ON a.cod_empresa = e.cod_empresa
JOIN sector s ON e.cod_sector = s.cod_sector
WHERE p.id_profesional IN (
    -- Subconsulta con Operador SET para filtrar versatilidad
    SELECT id_profesional 
    FROM asesoria a 
    JOIN empresa e ON a.cod_empresa = e.cod_empresa 
    WHERE e.cod_sector = 3 -- Banca
    INTERSECT
    SELECT id_profesional 
    FROM asesoria a 
    JOIN empresa e ON a.cod_empresa = e.cod_empresa 
    WHERE e.cod_sector = 4 -- Retail
)
GROUP BY p.id_profesional, p.appaterno, p.apmaterno, p.nombre
ORDER BY p.id_profesional ASC;


-- CASO 2: Creación de tabla REPORTE_MES
DROP TABLE REPORTE_MES;

CREATE TABLE REPORTE_MES AS
SELECT 
    p.id_profesional AS "ID_PROF",
    INITCAP(p.appaterno || ' ' || p.apmaterno || ' ' || p.nombre) AS "NOMBRE_COMPLETO",
    INITCAP(pr.nombre_profesion) AS "NOMBRE_PROFESION",
    INITCAP(c.nom_comuna) AS "NOM_COMUNA",
    COUNT(a.cod_empresa) AS "NRO_ASESORIAS",
    ROUND(SUM(a.honorario)) AS "MONTO_TOTAL_HONORARIOS",
    ROUND(AVG(a.honorario)) AS "PROMEDIO_HONORARIO",
    ROUND(MIN(a.honorario)) AS "HONORARIO_MINIMO",
    ROUND(MAX(a.honorario)) AS "HONORARIO_MAXIMO"
FROM profesional p
JOIN profesion pr ON p.cod_profesion = pr.cod_profesion
JOIN comuna c ON p.cod_comuna = c.cod_comuna
JOIN asesoria a ON p.id_profesional = a.id_profesional
WHERE 
    EXTRACT(MONTH FROM a.fin_asesoria) = 4 
    AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
GROUP BY 
    p.id_profesional, 
    p.appaterno, p.apmaterno, p.nombre, 
    pr.nombre_profesion, 
    c.nom_comuna
ORDER BY p.id_profesional ASC;

-- Verificación
SELECT * FROM REPORTE_MES;




-- 1. Consulta de Validación (ANTES del cambio)
SELECT 
    (SELECT SUM(honorario) 
     FROM asesoria a 
     WHERE a.id_profesional = p.id_profesional 
       AND EXTRACT(MONTH FROM a.fin_asesoria) = 3
       AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
    ) AS "HONORARIO_MARZO",
    p.id_profesional,
    p.numrun_prof,
    p.sueldo AS "SUELDO_ACTUAL"
FROM profesional p
WHERE EXISTS (
    SELECT 1 FROM asesoria a 
    WHERE a.id_profesional = p.id_profesional 
    AND EXTRACT(MONTH FROM a.fin_asesoria) = 3
    AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
)
ORDER BY p.id_profesional;

-- 2. Sentencia UPDATE (Actualización de datos)
UPDATE profesional p
SET sueldo = ROUND(sueldo * (
    CASE 
        WHEN (
            SELECT SUM(honorario) 
            FROM asesoria a 
            WHERE a.id_profesional = p.id_profesional 
              AND EXTRACT(MONTH FROM a.fin_asesoria) = 3
              AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
        ) < 1000000 THEN 1.10 -- Aumento del 10%
        ELSE 1.15 -- Aumento del 15% (si es >= 1.000.000)
    END
), 0)
WHERE EXISTS (
    -- Solo actualizamos a quienes tuvieron asesorías en Marzo del año pasado
    SELECT 1 
    FROM asesoria a 
    WHERE a.id_profesional = p.id_profesional 
      AND EXTRACT(MONTH FROM a.fin_asesoria) = 3
      AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
);

-- Confirmar cambios
COMMIT;

-- 3. Consulta de Validación (DESPUÉS del cambio para verificar)
SELECT 
    (SELECT SUM(honorario) 
     FROM asesoria a 
     WHERE a.id_profesional = p.id_profesional 
       AND EXTRACT(MONTH FROM a.fin_asesoria) = 3
       AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
    ) AS "HONORARIO_MARZO",
    p.id_profesional,
    p.numrun_prof,
    p.sueldo AS "SUELDO_NUEVO"
FROM profesional p
WHERE EXISTS (
    SELECT 1 FROM asesoria a 
    WHERE a.id_profesional = p.id_profesional 
    AND EXTRACT(MONTH FROM a.fin_asesoria) = 3
    AND EXTRACT(YEAR FROM a.fin_asesoria) = EXTRACT(YEAR FROM SYSDATE) - 1
)
ORDER BY p.id_profesional;