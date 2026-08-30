#!/usr/bin/env python3
"""
Generador de datos de gran volumen para las pruebas de rendimiento (S3, criterio 4).

Produce los tres CSV (transacciones, intereses, cuentas_anuales) con el mismo
formato y tipos de anomalias que el conjunto oficial, en el volumen que se
indique. La proporcion de anomalias se mantiene baja (~2%) para que las
pruebas de rendimiento midan trabajo util; el manejo de errores ya se
evidencia sobre los datos oficiales.

Uso:
    python scripts/generar_datos.py 50000
    python scripts/generar_datos.py 100000 src/main/resources/data/volumen_xl

El primer argumento es la cantidad de filas por archivo. El segundo (opcional)
es la carpeta de salida (por defecto: src/main/resources/data/volumen_xl).
"""
import csv, os, random, sys

def gen(n, carpeta):
    os.makedirs(carpeta, exist_ok=True)
    random.seed(42)

    fechas_ok = ["2024-01-15", "2024-03-22", "2024-06-30", "2024-09-10", "2024-12-01"]
    fechas_malas = ["2024/01/04", "03-04-2024", "17/06/2024", "2024-13-01"]  # ultima invalida
    tipos_ok = ["debito", "credito"]
    tipos_malos = ["invalid", "desconocido"]

    # transacciones.csv
    with open(f"{carpeta}/transacciones.csv", "w", newline="") as f:
        w = csv.writer(f); w.writerow(["id","fecha","monto","tipo"])
        for i in range(1, n+1):
            r = random.random()
            if r < 0.98:    # valida
                w.writerow([i, random.choice(fechas_ok), random.randint(100,5000), random.choice(tipos_ok)])
            elif r < 0.990: # monto invalido
                w.writerow([i, random.choice(fechas_ok), random.choice([-200,0,""]), random.choice(tipos_ok)])
            elif r < 0.995: # tipo invalido
                w.writerow([i, random.choice(fechas_ok), random.randint(100,5000), random.choice(tipos_malos)])
            else:           # fecha mala
                w.writerow([i, random.choice(fechas_malas), random.randint(100,5000), random.choice(tipos_ok)])

    # intereses.csv
    nombres = ["John Doe","Jane Smith","Bob Johnson","Alice Brown","Charlie Green","Diana Prince","Steve Rogers"]
    tipos_int = ["ahorro","prestamo","hipoteca"]
    with open(f"{carpeta}/intereses.csv", "w", newline="") as f:
        w = csv.writer(f); w.writerow(["cuenta_id","nombre","saldo","edad","tipo"])
        for i in range(1, n+1):
            r = random.random()
            if r < 0.98:
                w.writerow([100+i, random.choice(nombres), random.randint(1000,15000), random.randint(18,75), random.choice(tipos_int)])
            elif r < 0.990: # saldo invalido
                w.writerow([100+i, random.choice(nombres), random.choice([0,""]), random.randint(18,75), random.choice(tipos_int)])
            elif r < 0.995: # edad fuera de rango
                w.writerow([100+i, random.choice(nombres), random.randint(1000,15000), random.choice([100,150,""]), random.choice(tipos_int)])
            else:           # tipo desconocido
                w.writerow([100+i, random.choice(nombres), random.randint(1000,15000), random.randint(18,75), "-1"])

    # cuentas_anuales.csv
    trans = ["deposito","retiro","compra"]
    desc = ["Ingreso mensual","Retiro parcial","Compra en tienda","Ingreso extra",""]
    with open(f"{carpeta}/cuentas_anuales.csv", "w", newline="") as f:
        w = csv.writer(f); w.writerow(["cuenta_id","fecha","transaccion","monto","descripcion"])
        for i in range(1, n+1):
            r = random.random()
            cid = 100 + (i % 50)
            if r < 0.98:
                w.writerow([cid, random.choice(fechas_ok), random.choice(trans), random.choice([1000,2000,-500,3000]), random.choice(desc)])
            else:          # fecha o monto malo
                w.writerow([cid, random.choice(fechas_malas), random.choice(trans), random.choice(["",-100]), random.choice(desc)])

    print(f"Generados 3 CSV con {n} filas cada uno en: {carpeta}")

if __name__ == "__main__":
    n = int(sys.argv[1]) if len(sys.argv) > 1 else 50000
    carpeta = sys.argv[2] if len(sys.argv) > 2 else "src/main/resources/data/volumen_xl"
    gen(n, carpeta)
