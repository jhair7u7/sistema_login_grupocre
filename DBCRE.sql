CREATE DATABASE IF NOT EXISTS BDCRE
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE BDCRE;

-- Tabla: ROLES
-- Define los tipos de actor del sistema (RF-08: Administrador gestiona
-- ---------------------------------------------------------------------
CREATE TABLE roles (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMINISTRADOR', 'Gestiona usuarios bloqueados y el registro de colaboradores'),
    ('USUARIO', 'Accede al sistema mediante autenticación');

-- ---------------------------------------------------------------------
-- Tabla: USUARIOS
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL UNIQUE,
    username           VARCHAR(50)  NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    rol_id             INT NOT NULL,
    estado             ENUM('ACTIVO', 'BLOQUEADO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    intentos_fallidos  INT NOT NULL DEFAULT 0,
    fecha_bloqueo      DATETIME NULL,
    fecha_creacion     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                          ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- ---------------------------------------------------------------------
-- Tabla: TOKENS_RECUPERACION
-- RF-06: recuperación de contraseña vía correo electrónico
-- ---------------------------------------------------------------------
CREATE TABLE tokens_recuperacion (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id         INT NOT NULL,
    token              VARCHAR(255) NOT NULL UNIQUE,
    fecha_expiracion   DATETIME NOT NULL,
    usado              BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_token_rec_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Tabla: TOKENS_SESION
-- ---------------------------------------------------------------------
CREATE TABLE tokens_sesion (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id         INT NOT NULL,
    token              VARCHAR(500) NOT NULL UNIQUE,
    fecha_expiracion   DATETIME NOT NULL,
    revocado           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_token_ses_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Tabla: LOG_ACCESOS
-- ---------------------------------------------------------------------
CREATE TABLE log_accesos (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id         INT NULL,                        -- puede ser NULL si el usuario no existe
    tipo_evento        ENUM('LOGIN', 'LOGOUT', 'INTENTO_FALLIDO', 'BLOQUEO', 'RECUPERACION') NOT NULL,
    ip_address         VARCHAR(45),
    exitoso            BOOLEAN NOT NULL,
    fecha              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- Índices para optimizar las consultas más frecuentes del login
-- ---------------------------------------------------------------------
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_log_usuario ON log_accesos(usuario_id);


UPDATE usuarios SET rol_id = 1 WHERE email = 'carlos@example.com';
