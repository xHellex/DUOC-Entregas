-- CASO 1: Clientes en rango de renta (variables: &RENTA_MINIMA &RENTA_MAXIMA)
SELECT
  -- Formateo RUT: agrupador de miles + guion + dígito verificador
  TO_CHAR(NUMRUT_CLI, 'FM999G999G999', 'NLS_NUMERIC_CHARACTERS='',.''') || '-' || DVRUT_CLI AS "RUT Cliente",
  INITCAP(NOMBRE_CLI || ' ' || APPATERNO_CLI || ' ' || APMATERNO_CLI) AS "Nombre Cliente",
  TO_CHAR(ROUND(RENTA_CLI), 'L9G999G990', 'NLS_NUMERIC_CHARACTERS='',.''') AS "Renta",
  CASE
    WHEN ROUND(RENTA_CLI) > 500000 THEN 'TRAMO 1'
    WHEN ROUND(RENTA_CLI) BETWEEN 400000 AND 500000 THEN 'TRAMO 2'
    WHEN ROUND(RENTA_CLI) BETWEEN 200000 AND 399999 THEN 'TRAMO 3'
    ELSE 'TRAMO 4'
  END AS "Clasificación Renta"
FROM CLIENTE
WHERE
  CELULAR_CLI IS NOT NULL
  AND ROUND(RENTA_CLI) BETWEEN &RENTA_MINIMA AND &RENTA_MAXIMA
ORDER BY "Nombre Cliente" ASC;

-- CASO 2: Sueldo promedio por categoría y sucursal (variable: &SUELDO_PROMEDIO_MINIMO)
SELECT
  DECODE(ID_CATEGORIA_EMP,
         1, 'Gerente',
         2, 'Supervisor',
         3, 'Ejecutivo de Arriendo',
         4, 'Auxiliar',
         'Otro') AS "Categoría",
  DECODE(ID_SUCURSAL,
         10, 'Sucursal Las Condes',
         20, 'Sucursal Santiago Centro',
         30, 'Sucursal Providencia',
         40, 'Sucursal Vitacura',
         'Otra') AS "Sucursal",
  COUNT(*) AS "Cantidad Empleados",
  TO_CHAR(ROUND(AVG(NVL(SUELDO_EMP,0)), 0),
          'L9G999G990',
          'NLS_NUMERIC_CHARACTERS='',.''') AS "Sueldo Promedio"
FROM EMPLEADO
GROUP BY ID_CATEGORIA_EMP, ID_SUCURSAL
HAVING ROUND(AVG(NVL(SUELDO_EMP,0)),0) > &SUELDO_PROMEDIO_MINIMO
ORDER BY AVG(NVL(SUELDO_EMP,0)) DESC;

    
-- CASO 3: Arriendo promedio por tipo de propiedad
SELECT
  DECODE(ID_TIPO_PROPIEDAD,
         'A','CASA',
         'B','DEPARTAMENTO',
         'C','LOCAL',
         'D','PARCELA SIN CASA',
         'E','PARCELA CON CASA',
         'OTRO') AS "Tipo Propiedad",
  COUNT(*) AS "Total Propiedades",
  TO_CHAR(ROUND(AVG(VALOR_ARRIENDO),0),
          'L9G999G990',
          'NLS_NUMERIC_CHARACTERS='',.''') AS "Promedio Arriendo",
  TO_CHAR(ROUND(AVG(SUPERFICIE),0),'FM999G999') || ' m2' AS "Promedio Superficie",
  TO_CHAR(ROUND(AVG(CASE WHEN SUPERFICIE > 0 THEN VALOR_ARRIENDO / SUPERFICIE END),0),
          'L9G999G990',
          'NLS_NUMERIC_CHARACTERS='',.''') AS "Valor Arriendo por m2",
  CASE
    WHEN AVG(CASE WHEN SUPERFICIE > 0 THEN VALOR_ARRIENDO / SUPERFICIE END) < 5000 THEN 'Económico'
    WHEN AVG(CASE WHEN SUPERFICIE > 0 THEN VALOR_ARRIENDO / SUPERFICIE END) BETWEEN 5000 AND 10000 THEN 'Medio'
    ELSE 'Alto'
  END AS "Clasificación"
FROM PROPIEDAD
GROUP BY ID_TIPO_PROPIEDAD
HAVING AVG(CASE WHEN SUPERFICIE > 0 THEN VALOR_ARRIENDO / SUPERFICIE END) > 1000
ORDER BY AVG(CASE WHEN SUPERFICIE > 0 THEN VALOR_ARRIENDO / SUPERFICIE END) DESC;
