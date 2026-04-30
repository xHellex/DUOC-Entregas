-- =============================================================================
-- CASO 1: Listado de Trabajadores
-- =============================================================================
SELECT 
    UPPER(t.nombre || ' ' || t.appaterno || ' ' || t.apmaterno) AS "Nombre Completo Trabajador",
    TO_CHAR(t.numrut, '99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') || '-' || t.dvrut AS "RUT Trabajador",
    UPPER(tp.desc_categoria) AS "Tipo Trabajador",
    UPPER(cc.nombre_ciudad) AS "Ciudad Trabajador",
    TO_CHAR(t.sueldo_base, '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "Sueldo Base"
FROM 
    trabajador t
    JOIN tipo_trabajador tp ON t.id_categoria_t = tp.id_categoria
    JOIN comuna_ciudad cc ON t.id_ciudad = cc.id_ciudad
WHERE 
    t.sueldo_base BETWEEN &RENTA_MINIMA AND &RENTA_MAXIMA
ORDER BY 
    "Ciudad Trabajador" DESC, 
    t.sueldo_base ASC;


-- =============================================================================
-- CASO 2: Listado Cajeros
-- =============================================================================
SELECT 
    TO_CHAR(t.numrut, '99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') || '-' || t.dvrut AS "RUT Trabajador",
    INITCAP(t.nombre || ' ' || t.appaterno) AS "Nombre Trabajador",
    COUNT(tc.nro_ticket) AS "Total Tickets",
    TO_CHAR(SUM(tc.monto_ticket), '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "Total Vendido",
    TO_CHAR(SUM(ct.valor_comision), '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "Comisión Total",
    UPPER(tp.desc_categoria) AS "Tipo Trabajador",
    UPPER(cc.nombre_ciudad) AS "Ciudad Trabajador"
FROM 
    trabajador t
    JOIN tipo_trabajador tp ON t.id_categoria_t = tp.id_categoria
    JOIN comuna_ciudad cc ON t.id_ciudad = cc.id_ciudad
    JOIN tickets_concierto tc ON t.numrut = tc.numrut_t
    JOIN comisiones_ticket ct ON tc.nro_ticket = ct.nro_ticket
WHERE 
    UPPER(tp.desc_categoria) = 'CAJERO'
GROUP BY 
    t.numrut, t.dvrut, t.nombre, t.appaterno, tp.desc_categoria, cc.nombre_ciudad
HAVING 
    SUM(tc.monto_ticket) > 50000
ORDER BY 
    SUM(tc.monto_ticket) DESC;


-- =============================================================================
-- CASO 3: Listado de Bonificaciones
-- =============================================================================
SELECT 
    TO_CHAR(t.numrut, '99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "RUT Trabajador",
    INITCAP(t.nombre || ' ' || t.appaterno) AS "Trabajador Nombre",
    EXTRACT(YEAR FROM t.fecing) AS "Año Ingreso",
    (EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM t.fecing)) AS "Años Antigüedad",
    COUNT(af.numrut_carga) AS "Num. Cargas Familiares",
    i.nombre_isapre AS "Nombre Isapre",
    TO_CHAR(t.sueldo_base, '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,''') AS "Sueldo Base",
    TO_CHAR(
        CASE 
            WHEN UPPER(i.nombre_isapre) = 'FONASA' THEN t.sueldo_base * 0.01 
            ELSE 0 
        END, 
        '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,'''
    ) AS "Bono Fonasa",
    TO_CHAR(
        CASE 
            WHEN (EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM t.fecing)) <= 10 THEN t.sueldo_base * 0.10
            ELSE t.sueldo_base * 0.15
        END, 
        '$99G999G999', 'NLS_NUMERIC_CHARACTERS=''.,'''
    ) AS "Bono Antigüedad",
    INITCAP(a.nombre_afp) AS "Nombre AFP",
    UPPER(ec_desc.desc_estcivil) AS "Estado Civil"
FROM 
    trabajador t
    JOIN isapre i ON t.cod_isapre = i.cod_isapre
    JOIN afp a ON t.cod_afp = a.cod_afp
    JOIN est_civil ec ON t.numrut = ec.numrut_t
    JOIN estado_civil ec_desc ON ec.id_estcivil_est = ec_desc.id_estcivil
    LEFT JOIN asignacion_familiar af ON t.numrut = af.numrut_t
WHERE 
    (ec.fecter_estcivil IS NULL OR ec.fecter_estcivil > SYSDATE)
GROUP BY 
    t.numrut, t.nombre, t.appaterno, t.fecing, i.nombre_isapre, 
    t.sueldo_base, a.nombre_afp, ec_desc.desc_estcivil
ORDER BY 
    t.numrut ASC;