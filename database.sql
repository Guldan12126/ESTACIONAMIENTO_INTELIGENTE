CREATE DATABASE IF NOT EXISTS estacionamiento_utp
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE estacionamiento_utp;

DROP TABLE IF EXISTS historial_movimientos;
DROP TABLE IF EXISTS vehiculos;
DROP TABLE IF EXISTS avisos;
DROP TABLE IF EXISTS espacios;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  codigo VARCHAR(9) NOT NULL UNIQUE,
  nombre VARCHAR(120) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol ENUM('administrador', 'alumno') NOT NULL DEFAULT 'alumno',
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE espacios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  codigo_espacio VARCHAR(5) NOT NULL UNIQUE,
  zona CHAR(1) NOT NULL,
  estado ENUM('disponible', 'ocupado', 'reservado') NOT NULL DEFAULT 'disponible',
  placa VARCHAR(10) NULL,
  usuario_id INT NULL,
  hora_ingreso DATETIME NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TABLE vehiculos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  placa VARCHAR(10) NOT NULL,
  tipo ENUM('auto', 'moto') NOT NULL,
  usuario_id INT NOT NULL,
  espacio_id INT NOT NULL,
  estado ENUM('dentro', 'fuera') NOT NULL DEFAULT 'dentro',
  hora_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hora_salida DATETIME NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (espacio_id) REFERENCES espacios(id),
  INDEX idx_placa_estado (placa, estado)
);

CREATE TABLE avisos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(80) NOT NULL,
  mensaje VARCHAR(200) NOT NULL,
  categoria ENUM('horario', 'seguridad', 'mantenimiento', 'norma', 'emergencia') NOT NULL,
  creado_por INT NOT NULL,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (creado_por) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE historial_movimientos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tipo ENUM('ingreso', 'salida') NOT NULL,
  placa VARCHAR(10) NOT NULL,
  tipo_vehiculo ENUM('auto', 'moto') NOT NULL,
  espacio_id INT NOT NULL,
  usuario_id INT NOT NULL,
  registrado_por INT NOT NULL,
  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (espacio_id) REFERENCES espacios(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (registrado_por) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO usuarios (codigo, nombre, password_hash, rol) VALUES
('u22210840', 'Brayan Meza', '$2y$10$i7plCTM.2KyewpdHfrlyY.VshJpW7d/EXxc4z3DVzifiqqEdQRonC', 'administrador');

INSERT INTO espacios (codigo_espacio, zona, estado) VALUES
('A1', 'A', 'disponible'), ('A2', 'A', 'disponible'), ('A3', 'A', 'disponible'), ('A4', 'A', 'disponible'),
('A5', 'A', 'disponible'), ('A6', 'A', 'disponible'), ('A7', 'A', 'disponible'), ('A8', 'A', 'reservado'),
('B1', 'B', 'disponible'), ('B2', 'B', 'disponible'), ('B3', 'B', 'disponible'), ('B4', 'B', 'disponible'),
('B5', 'B', 'disponible'), ('B6', 'B', 'disponible'), ('B7', 'B', 'disponible'), ('B8', 'B', 'reservado'),
('C1', 'C', 'disponible'), ('C2', 'C', 'disponible'), ('C3', 'C', 'disponible'), ('C4', 'C', 'disponible'),
('C5', 'C', 'disponible'), ('C6', 'C', 'disponible'), ('C7', 'C', 'disponible'), ('C8', 'C', 'reservado');

INSERT INTO avisos (titulo, mensaje, categoria, creado_por) VALUES
('Cierre del estacionamiento', 'El estacionamiento cierra a las 10:00 p.m.', 'horario', 1),
('Uso de casco', 'Usar casco obligatorio para motociclistas.', 'seguridad', 1),
('Zonas verdes', 'No estacionarse en zonas verdes.', 'norma', 1),
('Circulacion interna', 'Respetar las flechas de circulacion.', 'norma', 1);
