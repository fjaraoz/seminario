-- 2.1) Dependencia
CREATE TABLE IF NOT EXISTS Dependencia (
  idDependencia INT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(100) NOT NULL,
  sigla         VARCHAR(20)  NOT NULL,
  ubicacion     VARCHAR(150),
  estado        ENUM('Activa','Inactiva') NOT NULL DEFAULT 'Activa',
  UNIQUE KEY uq_dependencia_sigla (sigla)
) ENGINE=InnoDB;

-- 2.2) Usuario
CREATE TABLE IF NOT EXISTS Usuario (
  idUsuario INT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(50)  NOT NULL,
  nombre    VARCHAR(100) NOT NULL,
  email     VARCHAR(120) NOT NULL,
  activo    BOOLEAN NOT NULL DEFAULT TRUE,
  UNIQUE KEY uq_usuario_username (username),
  UNIQUE KEY uq_usuario_email    (email)
) ENGINE=InnoDB;

-- 2.3) Rol (valores fijos sugeridos)
CREATE TABLE IF NOT EXISTS Rol (
  idRol   TINYINT      NOT NULL PRIMARY KEY,
  nombre  ENUM('Lectura','Edicion','Administracion') NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2.4) UsuarioRol (relación N–N)
CREATE TABLE IF NOT EXISTS UsuarioRol (
  idUsuario INT NOT NULL,
  idRol     TINYINT NOT NULL,
  PRIMARY KEY (idUsuario, idRol),
  CONSTRAINT fk_ur_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ur_rol     FOREIGN KEY (idRol)     REFERENCES Rol(idRol)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 2.5) Equipo
CREATE TABLE IF NOT EXISTS Equipo (
  idEquipo       INT AUTO_INCREMENT PRIMARY KEY,
  idDependencia  INT NOT NULL,
  tipo           ENUM('PC','Impresora','Escaner','Monitor') NOT NULL,
  marca          VARCHAR(60) NOT NULL,
  modelo         VARCHAR(60) NOT NULL,
  nroSerie       VARCHAR(80) NOT NULL,
  estado         ENUM('Operativo','EnReparacion','Baja') NOT NULL DEFAULT 'Operativo',
  observaciones  TEXT,
  CONSTRAINT uq_equipo_nroSerie UNIQUE (nroSerie),
  CONSTRAINT fk_equipo_dependencia FOREIGN KEY (idDependencia) REFERENCES Dependencia(idDependencia)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  KEY idx_equipo_dependencia (idDependencia),
  KEY idx_equipo_estado      (estado)
) ENGINE=InnoDB;

-- 2.6) Reparacion
CREATE TABLE IF NOT EXISTS Reparacion (
  idReparacion    INT AUTO_INCREMENT PRIMARY KEY,
  idEquipo        INT NOT NULL,
  idTecnico       INT NULL,  -- FK opcional a Usuario (técnico responsable)
  fechaApertura   DATE NOT NULL,
  descripcionFalla TEXT NOT NULL,
  estado          ENUM('Pendiente','Asignada','EnCurso','EnEspera','Finalizada','NoReparable') NOT NULL DEFAULT 'Pendiente',
  fechaCierre     DATE NULL,
  observaciones   TEXT,
  CONSTRAINT fk_reparacion_equipo   FOREIGN KEY (idEquipo)  REFERENCES Equipo(idEquipo)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_reparacion_tecnico  FOREIGN KEY (idTecnico) REFERENCES Usuario(idUsuario)
    ON DELETE SET NULL ON UPDATE CASCADE,
  KEY idx_rep_equipo (idEquipo),
  KEY idx_rep_estado (estado)
) ENGINE=InnoDB;

-- 2.7) Auditoria
CREATE TABLE IF NOT EXISTS Auditoria (
  idAuditoria BIGINT AUTO_INCREMENT PRIMARY KEY,
  fechaHora   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  accion      ENUM(
    'AltaEquipo','ModEquipo','BajaEquipo',
    'NuevaReparacion','ActualizarReparacion',
    'AsignarRol','QuitarRol',
    'CrearUsuario','ModificarUsuario','DeshabilitarUsuario'
  ) NOT NULL,
  idUsuario   INT NOT NULL,
  tipoRecurso ENUM('Equipo','Reparacion','Usuario') NOT NULL,
  idRecurso   INT NOT NULL,
  detalleJson JSON NULL,
  CONSTRAINT fk_auditoria_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  KEY idx_auditoria_tipo (tipoRecurso, idRecurso),
  KEY idx_auditoria_usuario_fecha (idUsuario, fechaHora)
) ENGINE=InnoDB;
