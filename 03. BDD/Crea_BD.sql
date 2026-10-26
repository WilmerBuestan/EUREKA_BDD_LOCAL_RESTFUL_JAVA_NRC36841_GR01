/*==============================================================*/
/* EUREKABANK - Script DDL                                       */
/* Motor: MariaDB 10.4+ / MySQL 8                                */
/* Grupo 1 - NRC 36841 - Arquitectura de Software ESPE           */
/* Uso: ejecutar primero este script y luego Carga_Datos.sql      */
/*==============================================================*/

-- DROP: elimina la base si existe
DROP DATABASE IF EXISTS eurekabank;

-- CREATE: crea la base de datos
CREATE DATABASE eurekabank CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE eurekabank;

/*==============================================================*/
/* CREATE TABLE: tablas con sus claves primarias e índices       */
/*==============================================================*/
CREATE TABLE asignado (
  chr_asigcodigo char(6) NOT NULL,
  chr_sucucodigo char(3) NOT NULL,
  chr_emplcodigo char(4) NOT NULL,
  dtt_asigfechaalta date NOT NULL,
  dtt_asigfechabaja date DEFAULT NULL,
  PRIMARY KEY (chr_asigcodigo),
  KEY idx_asignado01 (chr_emplcodigo),
  KEY idx_asignado02 (chr_sucucodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE cargomantenimiento (
  chr_monecodigo char(2) NOT NULL,
  dec_cargMontoMaximo decimal(12,2) NOT NULL,
  dec_cargImporte decimal(12,2) NOT NULL,
  PRIMARY KEY (chr_monecodigo),
  KEY idx_cargomantenimiento01 (chr_monecodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE cliente (
  chr_cliecodigo char(5) NOT NULL,
  vch_cliepaterno varchar(25) NOT NULL,
  vch_cliematerno varchar(25) NOT NULL,
  vch_clienombre varchar(30) NOT NULL,
  chr_cliedni char(8) NOT NULL,
  vch_clieciudad varchar(30) NOT NULL,
  vch_cliedireccion varchar(50) NOT NULL,
  vch_clietelefono varchar(20) DEFAULT NULL,
  vch_clieemail varchar(50) DEFAULT NULL,
  PRIMARY KEY (chr_cliecodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE contador (
  vch_conttabla varchar(30) NOT NULL,
  int_contitem int(11) NOT NULL,
  int_contlongitud int(11) NOT NULL,
  PRIMARY KEY (vch_conttabla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE costomovimiento (
  chr_monecodigo char(2) NOT NULL,
  dec_costimporte decimal(12,2) NOT NULL,
  PRIMARY KEY (chr_monecodigo),
  KEY idx_costomovimiento (chr_monecodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE cuenta (
  chr_cuencodigo char(8) NOT NULL,
  chr_monecodigo char(2) NOT NULL,
  chr_sucucodigo char(3) NOT NULL,
  chr_emplcreacuenta char(4) NOT NULL,
  chr_cliecodigo char(5) NOT NULL,
  dec_cuensaldo decimal(12,2) NOT NULL,
  dtt_cuenfechacreacion date NOT NULL,
  vch_cuenestado varchar(15) NOT NULL DEFAULT 'ACTIVO',
  int_cuencontmov int(11) NOT NULL,
  chr_cuenclave char(6) NOT NULL,
  PRIMARY KEY (chr_cuencodigo),
  KEY idx_cuenta01 (chr_cliecodigo),
  KEY idx_cuenta02 (chr_emplcreacuenta),
  KEY idx_cuenta03 (chr_sucucodigo),
  KEY idx_cuenta04 (chr_monecodigo),
  CONSTRAINT chk_cuenta_chr_cuenestado CHECK (vch_cuenestado in ('ACTIVO','ANULADO','CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE empleado (
  chr_emplcodigo char(4) NOT NULL,
  vch_emplpaterno varchar(25) NOT NULL,
  vch_emplmaterno varchar(25) NOT NULL,
  vch_emplnombre varchar(30) NOT NULL,
  vch_emplciudad varchar(30) NOT NULL,
  vch_empldireccion varchar(50) DEFAULT NULL,
  PRIMARY KEY (chr_emplcodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE interesmensual (
  chr_monecodigo char(2) NOT NULL,
  dec_inteimporte decimal(12,2) NOT NULL,
  PRIMARY KEY (chr_monecodigo),
  KEY idx_interesmensual01 (chr_monecodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE logsession (
  ID int(11) NOT NULL AUTO_INCREMENT,
  chr_emplcodigo char(4) NOT NULL,
  fec_ingreso datetime NOT NULL,
  fec_salida datetime DEFAULT NULL,
  ip varchar(20) NOT NULL DEFAULT 'NONE',
  hostname varchar(100) NOT NULL DEFAULT 'NONE',
  PRIMARY KEY (ID),
  KEY fk_log_session_empleado (chr_emplcodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE modulo (
  int_moducodigo int(11) NOT NULL,
  vch_modunombre varchar(50) DEFAULT NULL,
  vch_moduestado varchar(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_moduestado in ('ACTIVO','ANULADO','CANCELADO')),
  PRIMARY KEY (int_moducodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE moneda (
  chr_monecodigo char(2) NOT NULL,
  vch_monedescripcion varchar(20) NOT NULL,
  PRIMARY KEY (chr_monecodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE movimiento (
  chr_cuencodigo char(8) NOT NULL,
  int_movinumero int(11) NOT NULL,
  dtt_movifecha date NOT NULL,
  chr_emplcodigo char(4) NOT NULL,
  chr_tipocodigo char(3) NOT NULL,
  dec_moviimporte decimal(12,2) NOT NULL,
  chr_cuenreferencia char(8) DEFAULT NULL,
  PRIMARY KEY (chr_cuencodigo,int_movinumero),
  KEY idx_movimiento01 (chr_tipocodigo),
  KEY idx_movimiento02 (chr_emplcodigo),
  KEY idx_movimiento03 (chr_cuencodigo),
  CONSTRAINT chk_Movimiento_importe4 CHECK (dec_moviimporte >= 0.0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE parametro (
  chr_paracodigo char(3) NOT NULL,
  vch_paradescripcion varchar(50) NOT NULL,
  vch_paravalor varchar(70) NOT NULL,
  vch_paraestado varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (chr_paracodigo),
  CONSTRAINT chk_parametro_vch_paraestado CHECK (vch_paraestado in ('ACTIVO','ANULADO','CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE permiso (
  chr_emplcodigo char(4) NOT NULL,
  int_moducodigo int(11) NOT NULL,
  vch_permestado varchar(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_permestado in ('ACTIVO','ANULADO','CANCELADO')),
  PRIMARY KEY (chr_emplcodigo,int_moducodigo),
  KEY FK_Permiso_Modulo (int_moducodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE sucursal (
  chr_sucucodigo char(3) NOT NULL,
  vch_sucunombre varchar(50) NOT NULL,
  vch_sucuciudad varchar(30) NOT NULL,
  vch_sucudireccion varchar(50) DEFAULT NULL,
  int_sucucontcuenta int(11) NOT NULL,
  PRIMARY KEY (chr_sucucodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE tipomovimiento (
  chr_tipocodigo char(3) NOT NULL,
  vch_tipodescripcion varchar(40) NOT NULL,
  vch_tipoaccion varchar(10) NOT NULL,
  vch_tipoestado varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (chr_tipocodigo),
  CONSTRAINT chk_tipomovimiento_vch_tipoaccion CHECK (vch_tipoaccion in ('INGRESO','SALIDA')),
  CONSTRAINT chk_tipomovimiento_vch_tipoestado CHECK (vch_tipoestado in ('ACTIVO','ANULADO','CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE usuario (
  chr_emplcodigo char(4) NOT NULL,
  vch_emplusuario varchar(20) NOT NULL,
  vch_emplclave varchar(50) NOT NULL,
  vch_emplestado varchar(15) DEFAULT 'ACTIVO' CHECK (vch_emplestado in ('ACTIVO','ANULADO','CANCELADO')),
  PRIMARY KEY (chr_emplcodigo),
  UNIQUE KEY U_Usuario_vch_emplusuario (vch_emplusuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*==============================================================*/
/* ALTER TABLE: claves foráneas (relaciones entre tablas)        */
/*==============================================================*/
ALTER TABLE asignado
  ADD CONSTRAINT fk_asignado_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES empleado (chr_emplcodigo);

ALTER TABLE asignado
  ADD CONSTRAINT fk_asignado_sucursal FOREIGN KEY (chr_sucucodigo) REFERENCES sucursal (chr_sucucodigo);

ALTER TABLE cargomantenimiento
  ADD CONSTRAINT fk_cargomantenimiento_moneda FOREIGN KEY (chr_monecodigo) REFERENCES moneda (chr_monecodigo);

ALTER TABLE costomovimiento
  ADD CONSTRAINT fk_costomovimiento_moneda FOREIGN KEY (chr_monecodigo) REFERENCES moneda (chr_monecodigo);

ALTER TABLE cuenta
  ADD CONSTRAINT fk_cuenta_cliente FOREIGN KEY (chr_cliecodigo) REFERENCES cliente (chr_cliecodigo);

ALTER TABLE cuenta
  ADD CONSTRAINT fk_cuenta_moneda FOREIGN KEY (chr_monecodigo) REFERENCES moneda (chr_monecodigo);

ALTER TABLE cuenta
  ADD CONSTRAINT fk_cuenta_sucursal FOREIGN KEY (chr_sucucodigo) REFERENCES sucursal (chr_sucucodigo);

ALTER TABLE cuenta
  ADD CONSTRAINT fk_cuente_empleado FOREIGN KEY (chr_emplcreacuenta) REFERENCES empleado (chr_emplcodigo);

ALTER TABLE interesmensual
  ADD CONSTRAINT fk_interesmensual_moneda FOREIGN KEY (chr_monecodigo) REFERENCES moneda (chr_monecodigo);

ALTER TABLE logsession
  ADD CONSTRAINT fk_log_session_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES empleado (chr_emplcodigo);

ALTER TABLE movimiento
  ADD CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (chr_cuencodigo) REFERENCES cuenta (chr_cuencodigo);

ALTER TABLE movimiento
  ADD CONSTRAINT fk_movimiento_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES empleado (chr_emplcodigo);

ALTER TABLE movimiento
  ADD CONSTRAINT fk_movimiento_tipomovimiento FOREIGN KEY (chr_tipocodigo) REFERENCES tipomovimiento (chr_tipocodigo);

ALTER TABLE permiso
  ADD CONSTRAINT FK_Permiso_Modulo FOREIGN KEY (int_moducodigo) REFERENCES modulo (int_moducodigo);

ALTER TABLE permiso
  ADD CONSTRAINT FK_Permiso_Usuario FOREIGN KEY (chr_emplcodigo) REFERENCES usuario (chr_emplcodigo);

ALTER TABLE usuario
  ADD CONSTRAINT FK_Usuario_Empleado FOREIGN KEY (chr_emplcodigo) REFERENCES empleado (chr_emplcodigo);

/*==============================================================*/
/* Usuario de la aplicación (opcional)                           */
/*==============================================================*/
-- CREATE USER IF NOT EXISTS 'eureka'@'localhost' IDENTIFIED BY 'admin';
-- GRANT ALL PRIVILEGES ON eurekabank.* TO 'eureka'@'localhost';
-- FLUSH PRIVILEGES;
