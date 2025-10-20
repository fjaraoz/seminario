-- 3.1) Roles
INSERT INTO Rol (idRol, nombre) VALUES
  (1,'Lectura'),(2,'Edicion'),(3,'Administracion')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- 3.2) Usuarios
INSERT INTO Usuario (username, nombre, email, activo) VALUES
  ('tecnico1','Técnico Soporte','tecnico1@org.local', TRUE),
  ('admin1','Jefa de Área','admin1@org.local', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), activo=VALUES(activo);

-- 3.3) Asignar roles
INSERT INTO UsuarioRol (idUsuario, idRol)
SELECT u.idUsuario, r.idRol
FROM Usuario u JOIN Rol r
WHERE (u.username='tecnico1' AND r.nombre IN ('Edicion'))
   OR (u.username='admin1'   AND r.nombre IN ('Administracion'))
ON DUPLICATE KEY UPDATE idRol=VALUES(idRol);

-- 3.4) Dependencias
INSERT INTO Dependencia (nombre, sigla, ubicacion, estado) VALUES
  ('Mesa de Entradas','ME','PB', 'Activa'),
  ('Recursos Humanos','RRHH','1er piso', 'Activa'),
  ('Informática','INF','Subsuelo', 'Activa')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), ubicacion=VALUES(ubicacion), estado=VALUES(estado);

-- 3.5) Equipos
INSERT INTO Equipo (idDependencia, tipo, marca, modelo, nroSerie, estado, observaciones)
VALUES
 ((SELECT idDependencia FROM Dependencia WHERE sigla='INF'),'PC','Dell','OptiPlex 7090','SN-PC-001','Operativo','Equipo estándar'),
 ((SELECT idDependencia FROM Dependencia WHERE sigla='INF'),'Impresora','HP','LaserJet M404','SN-IMP-010','Operativo','Oficina soporte'),
 ((SELECT idDependencia FROM Dependencia WHERE sigla='RRHH'),'Monitor','Samsung','S24F350','SN-MON-021','Operativo',NULL)
ON DUPLICATE KEY UPDATE estado=VALUES(estado), observaciones=VALUES(observaciones);

-- 3.6) Reparaciones (una abierta y una finalizada)
INSERT INTO Reparacion (idEquipo, idTecnico, fechaApertura, descripcionFalla, estado, fechaCierre, observaciones)
VALUES
 ((SELECT idEquipo FROM Equipo WHERE nroSerie='SN-IMP-010'),
  (SELECT idUsuario FROM Usuario WHERE username='tecnico1'),
  DATE_SUB(CURDATE(), INTERVAL 7 DAY),
  'Atasco de papel recurrente',
  'EnCurso', NULL, 'Se limpió rodillo; revisar engranajes'),

 ((SELECT idEquipo FROM Equipo WHERE nroSerie='SN-PC-001'),
  (SELECT idUsuario FROM Usuario WHERE username='tecnico1'),
  DATE_SUB(CURDATE(), INTERVAL 20 DAY),
  'No enciende',
  'Finalizada', DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'Cambio de fuente');
