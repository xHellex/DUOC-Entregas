CREATE DATABASE IF NOT EXISTS Cine_DB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;
USE Cine_DB;

CREATE TABLE IF NOT EXISTS Cartelera (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  director VARCHAR(50) NOT NULL,
  ano INT NOT NULL,
  duracion INT NOT NULL,
  genero ENUM('Accion','Comedia','Drama','Terror','CienciaFiccion','Romance','Documental') NOT NULL,
  creado_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY ux_titulo (titulo)
);
