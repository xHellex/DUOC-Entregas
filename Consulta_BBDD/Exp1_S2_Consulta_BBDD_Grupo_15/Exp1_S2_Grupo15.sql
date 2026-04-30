/* ============================================================
   PROYECTO: SHIVERSALE - SEMANA 2
   OBJETIVO: Creación, carga y consultas de la base de datos
   ============================================================ */

/* ---- CONFIGURACIÓN REGIONAL ---- */
ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY';


/* ---- BORRADO DE OBJETOS EXISTENTES ---- */
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE detalle_factura CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE factura CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE detalle_boleta CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE boleta CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE producto CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE unidad_medida CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE pais CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE cliente CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE vendedor CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE forma_pago CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE banco CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE comuna CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE ciudad CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE tramo_antiguedad CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE tramo_escolaridad CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE remun_mensual_vendedor CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE escolaridad CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

/* ---- CREACIÓN DE TABLAS PRINCIPALES ---- */

CREATE TABLE remun_mensual_vendedor (
  rutvendedor       VARCHAR2(10),
  nomvendedor       VARCHAR2(30),
  fecha_remun       NUMBER(6),
  sueldo_base       NUMBER(8),
  colacion          NUMBER(8),
  movilizacion      NUMBER(8),
  prevision         NUMBER(8),
  salud             NUMBER(8),
  comision_normal   NUMBER(8),
  comision_por_venta NUMBER(8),
  total_bonos       NUMBER(8),
  total_haberes     NUMBER(8),
  total_desctos     NUMBER(8),
  total_pagar       NUMBER(8),
  CONSTRAINT pk_remun_mensual_vendedor PRIMARY KEY (rutvendedor, fecha_remun)
);

CREATE TABLE tramo_antiguedad (
  sec_annos_contratado NUMBER(2),
  fec_ini_vig          DATE,
  fec_ter_vig          DATE,
  annos_cont_inf       NUMBER(2),
  annos_cont_sup       NUMBER(2),
  porcentaje           NUMBER(2),
  CONSTRAINT pk_tramo_antiguedad PRIMARY KEY (sec_annos_contratado, fec_ini_vig)
);

CREATE TABLE escolaridad (
  id_escolaridad    NUMBER(2),
  sigla_escolaridad VARCHAR2(5),
  desc_escolaridad  VARCHAR2(50),
  CONSTRAINT pk_escolaridad PRIMARY KEY (id_escolaridad)
);

CREATE TABLE tramo_escolaridad (
  id_escolaridad        NUMBER(2),
  fecha_vigencia        NUMBER(4),
  porc_asig_escolaridad NUMBER(2),
  CONSTRAINT pk_tramo_escolaridad PRIMARY KEY (id_escolaridad, fecha_vigencia),
  CONSTRAINT fk_tramo_escolaridad FOREIGN KEY (id_escolaridad) REFERENCES escolaridad(id_escolaridad)
);

CREATE TABLE ciudad (
  codciudad   NUMBER(2),
  descripcion VARCHAR2(30),
  CONSTRAINT pk_cod_ciudad PRIMARY KEY (codciudad)
);

CREATE TABLE comuna (
  codcomuna   NUMBER(2),
  descripcion VARCHAR2(30),
  codciudad   NUMBER(2),
  CONSTRAINT pk_cod_comuna PRIMARY KEY (codcomuna),
  CONSTRAINT cod_ciudad_fk FOREIGN KEY (codciudad) REFERENCES ciudad(codciudad)
);

CREATE TABLE cliente (
  rutcliente VARCHAR2(10),
  nombre     VARCHAR2(30),
  direccion  VARCHAR2(30),
  codcomuna  NUMBER(2),
  telefono   NUMBER(10),
  estado     VARCHAR2(1),
  mail       VARCHAR2(50),
  credito    NUMBER(7),
  saldo      NUMBER(7),
  CONSTRAINT pk_rut_cliente PRIMARY KEY (rutcliente),
  CONSTRAINT estado_cliente_ck CHECK (estado IN ('A', 'B')),
  CONSTRAINT cod_comuna_fk FOREIGN KEY (codcomuna) REFERENCES comuna(codcomuna)
);

CREATE TABLE vendedor (
  rutvendedor    VARCHAR2(10),
  id             NUMBER(2),
  nombre         VARCHAR2(30),
  direccion      VARCHAR2(30),
  codcomuna      NUMBER(2),
  telefono       NUMBER(10),
  mail           VARCHAR2(50),
  sueldo_base    NUMBER(8),
  comision       NUMBER(4,2),
  fecha_contrato DATE,
  id_escolaridad NUMBER(2),
  CONSTRAINT pk_rut_vendedor PRIMARY KEY (rutvendedor),
  CONSTRAINT vendedor_cod_comuna_fk FOREIGN KEY (codcomuna) REFERENCES comuna(codcomuna),
  CONSTRAINT fk_esc_vendedor FOREIGN KEY (id_escolaridad) REFERENCES escolaridad(id_escolaridad)
);

CREATE TABLE unidad_medida (
  codunidad   VARCHAR2(2),
  descripcion VARCHAR2(30),
  CONSTRAINT pk_cod_unidad PRIMARY KEY (codunidad)
);

CREATE TABLE pais (
  codpais NUMBER(2),
  nompais VARCHAR2(30),
  CONSTRAINT pk_pais PRIMARY KEY (codpais)
);

CREATE TABLE producto (
  codproducto      NUMBER(3),
  descripcion      VARCHAR2(40),
  codunidad        VARCHAR2(2),
  codcategoria     VARCHAR2(1),
  vunitario        NUMBER(8),
  valorcomprapeso  NUMBER(8),
  valorcompradolar NUMBER(8, 2),
  totalstock       NUMBER(5),
  stkseguridad     NUMBER(5),
  procedencia      VARCHAR2(1),
  codpais          NUMBER(2),
  codproducto_rel  NUMBER(3),
  CONSTRAINT pk_cod_prod PRIMARY KEY (codproducto),
  CONSTRAINT cod_unidad_fk FOREIGN KEY (codunidad) REFERENCES unidad_medida(codunidad),
  CONSTRAINT cod_pais_fk FOREIGN KEY (codpais) REFERENCES pais(codpais),
  CONSTRAINT codproducto_rel_fk FOREIGN KEY (codproducto_rel) REFERENCES producto(codproducto)
);

CREATE TABLE forma_pago (
  codpago     NUMBER(2),
  descripcion VARCHAR2(30),
  CONSTRAINT pk_codpago PRIMARY KEY (codpago)
);

CREATE TABLE banco (
  codbanco    NUMBER(2),
  descripcion VARCHAR2(30),
  CONSTRAINT pk_codbanco PRIMARY KEY (codbanco)
);

CREATE TABLE factura (
  numfactura     NUMBER(7),
  rutcliente     VARCHAR2(10),
  rutvendedor    VARCHAR2(10),
  fecha          DATE,
  f_vencimiento  DATE,
  neto           NUMBER(7),
  iva            NUMBER(7),
  total          NUMBER(7),
  codbanco       NUMBER(2),
  codpago        NUMBER(2),
  num_docto_pago VARCHAR2(30),
  estado         VARCHAR2(2),
  CONSTRAINT pk_numfactur PRIMARY KEY (numfactura),
  CONSTRAINT rutcliente_fk FOREIGN KEY (rutcliente) REFERENCES cliente(rutcliente),
  CONSTRAINT rutvendedor_fk FOREIGN KEY (rutvendedor) REFERENCES vendedor(rutvendedor),
  CONSTRAINT codpago_fk FOREIGN KEY (codpago) REFERENCES forma_pago(codpago),
  CONSTRAINT codbanco_fk FOREIGN KEY (codbanco) REFERENCES banco(codbanco),
  CONSTRAINT estado_factura_ck CHECK (estado IN ('EM','PA','NC'))
);

CREATE TABLE detalle_factura (
  numfactura   NUMBER(7),
  codproducto  NUMBER(3),
  vunitario    NUMBER(8),
  codpromocion NUMBER(4),
  descri_prom  VARCHAR2(60),
  descuento    NUMBER(8),
  cantidad     NUMBER(5),
  totallinea   NUMBER(8),
  CONSTRAINT pk_det_fact PRIMARY KEY (numfactura, codproducto),
  CONSTRAINT cod_prod_fk FOREIGN KEY (codproducto) REFERENCES producto(codproducto),
  CONSTRAINT num_fact_fk FOREIGN KEY (numfactura) REFERENCES factura(numfactura)
);

CREATE TABLE boleta (
  numboleta      NUMBER(7),
  rutcliente     VARCHAR2(10),
  rutvendedor    VARCHAR2(10),
  fecha          DATE,
  total          NUMBER(7),
  codpago        NUMBER(2),
  codbanco       NUMBER(2),
  num_docto_pago VARCHAR2(30),
  estado         VARCHAR2(2),
  CONSTRAINT pk_numboleta PRIMARY KEY (numboleta),
  CONSTRAINT bol_rutcliente_fk FOREIGN KEY (rutcliente) REFERENCES cliente(rutcliente),
  CONSTRAINT bol_rutvendedor_fk FOREIGN KEY (rutvendedor) REFERENCES vendedor(rutvendedor),
  CONSTRAINT bol_codpago_fk FOREIGN KEY (codpago) REFERENCES forma_pago(codpago),
  CONSTRAINT bol_codbanco_fk FOREIGN KEY (codbanco) REFERENCES banco(codbanco),
  CONSTRAINT bol_estado_boleta_ck CHECK (estado IN ('EM','PA','NC'))
);

CREATE TABLE detalle_boleta (
  numboleta    NUMBER(7),
  codproducto  NUMBER(3),
  vunitario    NUMBER(8),
  codpromocion NUMBER(4),
  descri_prom  VARCHAR2(60),
  descuento    NUMBER(8),
  cantidad     NUMBER(5),
  totallinea   NUMBER(8),
  CONSTRAINT pk_det_boleta PRIMARY KEY (numboleta, codproducto),
  CONSTRAINT det_bol_codproducto_fk FOREIGN KEY (codproducto) REFERENCES producto(codproducto),
  CONSTRAINT det_bol_num_boleta_fk FOREIGN KEY (numboleta) REFERENCES boleta(numboleta)
);

/* ---- INSERCIÓN DE DATOS ---- */
/* (se mantienen los INSERTS originales del caso para carga masiva) */

/* ---- CONSULTAS DE INFORME ---- */

-- INFORME 1: Análisis de facturas por monto y forma de pago
SELECT
    NUMFACTURA AS "N° Factura",
    TO_CHAR(FECHA, 'DD "de" Month', 'NLS_DATE_LANGUAGE=SPANISH') AS "Fecha Emisión",
    LPAD(RUTCLIENTE, 10, '0') AS "RUT Cliente",
    TO_CHAR(ROUND(NETO), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,''') AS "Monto Neto",
    TO_CHAR(ROUND(IVA), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,''') AS "Monto IVA",
    TO_CHAR(ROUND(TOTAL), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,''') AS "Total Factura",
    CASE
        WHEN TOTAL <= 50000 THEN 'Bajo'
        WHEN TOTAL > 50000 AND TOTAL <= 100000 THEN 'Medio'
        ELSE 'Alto'
    END AS "Categoría Monto",
    DECODE(CODPAGO, 1, 'EFECTIVO',
                     2, 'TARJETA DEBITO',
                     3, 'TARJETA CREDITO',
                     'CHEQUE') AS "Forma de Pago"
FROM FACTURA
WHERE EXTRACT(YEAR FROM FECHA) = EXTRACT(YEAR FROM SYSDATE) - 1
ORDER BY FECHA DESC, NETO DESC;


-- INFORME 2: Clasificación de clientes por crédito y dominio de correo
SELECT
    LPAD(RUTCLIENTE, 12, '*') AS "RUT",
    INITCAP(NOMBRE) AS "Cliente",
    NVL(TO_CHAR(TELEFONO), 'Sin teléfono') AS "Teléfono",
    NVL(TO_CHAR(CODCOMUNA), 'Sin comuna') AS "Comuna",
    ESTADO,
    CASE
        WHEN (SALDO / CREDITO) < 0.5 THEN 'Bueno (' || TO_CHAR(ROUND(CREDITO - SALDO), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,''') || ')'
        WHEN (SALDO / CREDITO) <= 0.8 THEN 'Regular (' || TO_CHAR(ROUND(SALDO), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,''') || ')'
        ELSE 'Crítico'
    END AS "Estado Crédito",
    NVL(UPPER(SUBSTR(MAIL, INSTR(MAIL, '@') + 1)), 'Correo no registrado') AS "Dominio Correo"
FROM CLIENTE
WHERE ESTADO = 'A' AND CREDITO > 0
ORDER BY "Cliente" ASC;


-- INFORME 3: Análisis de stock y rentabilidad de productos
SELECT
    CODPRODUCTO AS "ID",
    DESCRIPCION AS "Descripción de Producto",
    NVL(TO_CHAR(VALORCOMPRADOLAR, '9G990D00', 'NLS_NUMERIC_CHARACTERS='',.''') || ' USD', 'Sin registro') AS "Compra en USD",
    NVL(TO_CHAR(ROUND(VALORCOMPRADOLAR * &TIPOCAMBIO_DOLAR), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,'''), 'Sin registro') AS "USD convertido",
    TOTALSTOCK AS "Stock",
    CASE
        WHEN TOTALSTOCK IS NULL THEN 'Sin datos'
        WHEN TOTALSTOCK < &UMBRAL_BAJO THEN '¡ ALERTA stock muy bajo!'
        WHEN TOTALSTOCK <= &UMBRAL_ALTO THEN '¡Reabastecer pronto!'
        ELSE 'OK'
    END AS "Alerta Stock",
    CASE
        WHEN TOTALSTOCK > 80 THEN TO_CHAR(ROUND(VUNITARIO * 0.9), 'L9G999G990', 'NLS_CURRENCY=''$'' NLS_NUMERIC_CHARACTERS=''.,''')
        ELSE 'N/A'
    END AS "Precio Oferta"
FROM PRODUCTO
WHERE LOWER(DESCRIPCION) LIKE '%zapato%' AND PROCEDENCIA = 'I'
ORDER BY CODPRODUCTO DESC;

COMMIT;
