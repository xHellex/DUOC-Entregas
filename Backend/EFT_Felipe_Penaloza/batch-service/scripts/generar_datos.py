#!/usr/bin/env python3
"""
Generador de datos de gran volumen para las pruebas de rendimiento (S3, criterio 4).

Toma el dataset oficial fin_legacy_data en data/ (1000 registros con inconsistencias) y lo
replica N veces para alcanzar el volumen deseado, conservando exactamente la
misma proporcion y variedad de anomalias. Asi la comparacion de configuraciones
de particionamiento se hace sobre datos representativos del oficial, no sobre
datos inventados.

Uso:
    python scripts/generar_datos.py 20        -> 20 x 1000 = 20.000 filas
    python scripts/generar_datos.py 100 xl    -> 100.000 filas en data/volumen_xl

Primer argumento: factor de replicacion. Segundo (opcional): sufijo de carpeta.
"""
import csv, os, sys

BASE = "src/main/resources/data"

def replicar(factor, carpeta):
    os.makedirs(carpeta, exist_ok=True)
    for archivo in ["transacciones.csv", "intereses.csv", "cuentas_anuales.csv"]:
        origen = os.path.join(BASE, archivo)
        with open(origen, newline="") as f:
            filas = list(csv.reader(f))
        cabecera, datos = filas[0], filas[1:]

        destino = os.path.join(carpeta, archivo)
        with open(destino, "w", newline="") as f:
            w = csv.writer(f)
            w.writerow(cabecera)
            idx = 1
            for _ in range(factor):
                for fila in datos:
                    nueva = list(fila)
                    # Reasignar el id/primer campo para que sea unico y creciente
                    if nueva and nueva[0].strip().isdigit():
                        nueva[0] = str(idx)
                    w.writerow(nueva)
                    idx += 1
        print(f"  {archivo}: {len(datos)*factor} filas -> {destino}")

    print(f"Dataset de {len(datos)*factor} filas generado en {carpeta} "
          f"(replicando el oficial x{factor}).")

if __name__ == "__main__":
    factor = int(sys.argv[1]) if len(sys.argv) > 1 else 20
    sufijo = sys.argv[2] if len(sys.argv) > 2 else "grande"
    replicar(factor, os.path.join(BASE, "volumen_" + sufijo))
