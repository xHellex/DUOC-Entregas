# Despliegue en la nube (AWS EC2)

Guía para desplegar el ecosistema del Banco XYZ en una instancia **AWS EC2** usando Docker Compose. Cubre el requerimiento de "preparar los microservicios para un entorno de nube (AWS)" y la escalabilidad horizontal.

---

## 1. Provisionar la instancia EC2

1. En la consola de AWS → **EC2 → Launch instance**.
2. AMI: **Ubuntu Server 22.04 LTS**.
3. Tipo: **t3.large** (2 vCPU / 8 GB) como mínimo — son 8 servicios Java + Kafka. Para el escalado con réplicas, **t3.xlarge** (16 GB).
4. Almacenamiento: 30 GB gp3.
5. Crear/usar un **key pair** para SSH.

## 2. Security Group (puertos)

Abrir entradas (inbound) según lo que quieras exponer:

| Puerto | Uso | Origen recomendado |
|--------|-----|--------------------|
| 22 | SSH | Tu IP |
| 8080 | API Gateway | Tu IP / público |
| 8761 | Eureka (evidencia) | Tu IP |
| 9000 | Auth Server (token) | Tu IP |
| 8090 | Kafka-UI (evidencia) | Tu IP |
| 9094 | Kafka listener externo (solo si clientes fuera de EC2) | Tu IP |

> Principio de mínimo privilegio: expón solo lo necesario y restringe el origen a tu IP.

## 3. Instalar Docker en la instancia

```bash
ssh -i mi-clave.pem ubuntu@<IP_PUBLICA_EC2>

sudo apt-get update
sudo apt-get install -y docker.io docker-compose-v2 git
sudo usermod -aG docker ubuntu
newgrp docker
docker --version && docker compose version
```

## 4. Subir el proyecto

```bash
# Opción A: clonar desde GitHub
git clone https://github.com/xHellex/DUOC-Entregas.git
cd DUOC-Entregas/Backend/EFT_Felipe_Penaloza

# Opción B: copiar desde tu equipo con scp
# scp -i mi-clave.pem -r EFT_Felipe_Penaloza ubuntu@<IP_PUBLICA_EC2>:~/
```

## 5. Levantar el ecosistema

```bash
docker compose up --build -d
docker compose ps        # todos los servicios Up
docker images            # imágenes bancoxyz/*
```

Accede desde tu navegador:
- Eureka: `http://<IP_PUBLICA_EC2>:8761`
- Gateway: `http://<IP_PUBLICA_EC2>:8080`
- Kafka-UI: `http://<IP_PUBLICA_EC2>:8090`

## 6. Escalado horizontal en la nube

```bash
docker compose up -d --scale ms-pagos=3 --scale ms-cuentas=2 --scale ms-clientes=2
docker compose ps
```
En Eureka verás varias instancias por servicio; el gateway reparte la carga.

---

## 7. Kafka accesible desde fuera de EC2 (opcional)

Si además quieres que **microservicios fuera de la instancia** (p. ej. en tu equipo o en el homelab) se conecten al broker de EC2, el *advertised listener* externo debe anunciar el DNS/IP público:

En `docker-compose.yml`, servicio `kafka`, cambia el listener externo:

```yaml
      KAFKA_ADVERTISED_LISTENERS: "PLAINTEXT://kafka:9092,EXTERNAL://<DNS_PUBLICO_EC2>:9094"
```

- Abre el puerto **9094** en el Security Group.
- En el microservicio local, apunta `spring.kafka.bootstrap-servers` a `<DNS_PUBLICO_EC2>:9094`.
- Reinicia: `docker compose up -d kafka`.

> Los servicios que corren dentro del mismo compose siguen usando `kafka:9092` (listener interno); el externo es solo para clientes fuera de la instancia.

---

## 8. Evidencia de despliegue a capturar

1. Consola de EC2 mostrando la instancia **running** (tipo, IP pública).
2. `docker compose ps` / `docker ps` **dentro de la instancia** con todos los contenedores Up.
3. `docker images` con las imágenes `bancoxyz/*`.
4. Eureka (`:8761`) accedido por IP pública con los servicios registrados (y varias instancias al escalar).
5. Obtención de token y una llamada protegida (200) contra `http://<IP_PUBLICA_EC2>:8080`.
6. Kafka-UI (`:8090`) con el tópico `pagos` y mensajes; logs de los consumidores.

## 9. Buenas prácticas / próximos pasos

- Variables sensibles (client-secret, credenciales) fuera del compose (archivo `.env` o AWS Secrets Manager).
- Persistencia gestionada: migrar de H2 a **Amazon RDS (MySQL/PostgreSQL)**.
- Reinicio automático: `restart: unless-stopped` por servicio.
- Para producción real: orquestar con **ECS/EKS** en vez de Docker Compose en una sola instancia.
- Monitoreo y logs centralizados (CloudWatch).
