-- CASO 1: Listado de Clientes (Trabajadores Dependientes - Contador/Vendedor)
SELECT 
    TO_CHAR(c.numrun, 'FM09G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') || '-' || c.dvrun AS "RUT Cliente",
    INITCAP(c.pnombre || ' ' || c.appaterno) AS "Nombre Cliente",
    UPPER(po.nombre_prof_ofic) AS "Profesión Cliente",
    TO_CHAR(c.fecha_inscripcion, 'DD-MM-YYYY') AS "Fecha de Inscripción",
    c.direccion AS "Dirección Cliente"
FROM 
    cliente c
    JOIN profesion_oficio po ON c.cod_prof_ofic = po.cod_prof_ofic
    JOIN tipo_cliente tc ON c.cod_tipo_cliente = tc.cod_tipo_cliente
WHERE 
    UPPER(tc.nombre_tipo_cliente) LIKE '%TRABAJADORES DEPENDIENTES%'
    AND UPPER(po.nombre_prof_ofic) IN ('CONTADOR', 'VENDEDOR')
    AND EXTRACT(YEAR FROM c.fecha_inscripcion) > (
        SELECT ROUND(AVG(EXTRACT(YEAR FROM fecha_inscripcion)))
        FROM cliente
    )
ORDER BY 
    c.numrun ASC;
    
    
    
-- Primero eliminamos la tabla si ya existe (para evitar errores al re-ejecutar)
DROP TABLE CLIENTES_CUPOS_COMPRA;

-- CASO 2: Creación de tabla CLIENTES_CUPOS_COMPRA con subconsulta
CREATE TABLE CLIENTES_CUPOS_COMPRA AS
SELECT 
    TO_CHAR(c.numrun, 'FM09G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') || '-' || c.dvrun AS "RUT_CLIENTE",
    TRUNC(MONTHS_BETWEEN(SYSDATE, c.fecha_nacimiento) / 12) AS "EDAD",
    TO_CHAR(tc.cupo_disp_compra, '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "CUPO_DISPONIBLE_COMPRA",
    UPPER(tic.nombre_tipo_cliente) AS "TIPO_CLIENTE"
FROM 
    cliente c
    JOIN tarjeta_cliente tc ON c.numrun = tc.numrun
    JOIN tipo_cliente tic ON c.cod_tipo_cliente = tic.cod_tipo_cliente
WHERE 
    tc.cupo_disp_compra >= (
        SELECT MAX(cupo_disp_compra)
        FROM tarjeta_cliente
        WHERE EXTRACT(YEAR FROM fecha_solic_tarjeta) = EXTRACT(YEAR FROM SYSDATE) - 1
    )
ORDER BY 
    "EDAD" ASC;

-- Verificación: Consultar la tabla creada para ver los resultados
SELECT * FROM CLIENTES_CUPOS_COMPRA;