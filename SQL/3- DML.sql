-- 3.1) Roles
INSERT INTO Rol (idRol, nombre) VALUES
  (1,'Lectura'),(2,'Edicion'),(3,'Administracion')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- 3.2) Usuarios
INSERT INTO Usuario (username, nombre, email, activo) VALUES
  ('tecnico1','Tecnico Soporte','tecnico1@org.local', TRUE),
  ('admin1','Jefa de Area','admin1@org.local', TRUE)
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
	('Informatica','INF','Subsuelo', 'Activa'),
	('Contabilidad','CONT','2do piso', 'Activa'),
    ('Finanzas','FIN','3er piso', 'Activa'),
    ('Cocina','COC','Subsuelo', 'Activa'),
    ('Legales','LEG','4to piso', 'Activa'),
    ('Direccion','DIR','5to piso', 'Activa')

  
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), ubicacion=VALUES(ubicacion), estado=VALUES(estado);

-- 3.5) Equipos
-- Equipos por dependencia

INSERT INTO Equipo (idDependencia, tipo, marca, modelo, nroSerie, estado, observaciones) VALUES
-- Mesa de Entradas (3 PCs, 2 impresoras)
((SELECT idDependencia FROM Dependencia WHERE nombre='Mesa de Entradas'),
 'PC','Dell','OptiPlex 3080','ME-PC-001','Operativo','ME-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Mesa de Entradas'),
 'PC','Dell','OptiPlex 3080','ME-PC-002','Operativo','ME-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Mesa de Entradas'),
 'PC','HP','ProDesk 400','ME-PC-003','Operativo','ME-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Mesa de Entradas'),
 'Impresora','HP','LaserJet Pro M203','ME-IMP-001','Operativo','ME-INV-101'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Mesa de Entradas'),
 'Impresora','Epson','L3250','ME-IMP-002','Operativo','ME-INV-102'),

-- Recursos Humanos (4 PCs, 2 impresoras)
((SELECT idDependencia FROM Dependencia WHERE nombre='Recursos Humanos'),
 'PC','Lenovo','ThinkCentre M720','RRHH-PC-001','Operativo','RRHH-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Recursos Humanos'),
 'PC','Lenovo','ThinkCentre M720','RRHH-PC-002','Operativo','RRHH-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Recursos Humanos'),
 'PC','HP','EliteDesk 800','RRHH-PC-003','Operativo','RRHH-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Recursos Humanos'),
 'PC','HP','EliteDesk 800','RRHH-PC-004','Operativo','RRHH-INV-004'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Recursos Humanos'),
 'Impresora','Brother','HL-L2370DN','RRHH-IMP-001','Operativo','RRHH-INV-101'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Recursos Humanos'),
 'Impresora','HP','LaserJet Pro M404','RRHH-IMP-002','Operativo','RRHH-INV-102'),

-- Informática (6 PCs, 3 impresoras)
((SELECT idDependencia FROM Dependencia WHERE nombre='Informática'),
 'PC','Dell','OptiPlex 5080','INF-PC-001','Operativo','INF-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'PC','Dell','OptiPlex 5080','INF-PC-002','Operativo','INF-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'PC','Dell','OptiPlex 5080','INF-PC-003','Operativo','INF-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'PC','HP','Z2 G5','INF-PC-004','Operativo','INF-INV-004'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'PC','Lenovo','ThinkStation P340','INF-PC-005','Operativo','INF-INV-005'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'PC','Lenovo','ThinkStation P340','INF-PC-006','Operativo','INF-INV-006'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'Impresora','HP','LaserJet Pro M404','INF-IMP-001','Operativo','INF-INV-101'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'Impresora','Brother','DCP-L2550DW','INF-IMP-002','Operativo','INF-INV-102'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Informatica'),
 'Impresora','Epson','L4260','INF-IMP-003','Operativo','INF-INV-103'),

-- Contabilidad (5 PCs, 2 impresoras)
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'PC','HP','ProDesk 400','CONT-PC-001','Operativo','CONT-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'PC','HP','ProDesk 400','CONT-PC-002','Operativo','CONT-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'PC','Dell','OptiPlex 3080','CONT-PC-003','Operativo','CONT-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'PC','Dell','OptiPlex 3080','CONT-PC-004','Operativo','CONT-INV-004'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'PC','Lenovo','ThinkCentre M720','CONT-PC-005','Operativo','CONT-INV-005'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'Impresora','HP','LaserJet Pro M203','CONT-IMP-001','Operativo','CONT-INV-101'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Contabilidad'),
 'Impresora','Epson','L3250','CONT-IMP-002','Operativo','CONT-INV-102'),

-- Finanzas (4 PCs, 2 impresoras)
((SELECT idDependencia FROM Dependencia WHERE nombre='Finanzas'),
 'PC','Lenovo','ThinkCentre M720','FIN-PC-001','Operativo','FIN-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Finanzas'),
 'PC','Lenovo','ThinkCentre M720','FIN-PC-002','Operativo','FIN-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Finanzas'),
 'PC','HP','ProDesk 400','FIN-PC-003','Operativo','FIN-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Finanzas'),
 'PC','Dell','OptiPlex 3080','FIN-PC-004','Operativo','FIN-INV-004'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Finanzas'),
 'Impresora','HP','LaserJet Pro M404','FIN-IMP-001','Operativo','FIN-INV-101'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Finanzas'),
 'Impresora','Brother','HL-L2370DN','FIN-IMP-002','Operativo','FIN-INV-102'),

-- Cocina (2 PCs, 1 impresora)
((SELECT idDependencia FROM Dependencia WHERE nombre='Cocina'),
 'PC','HP','ProDesk 400','COC-PC-001','Operativo','COC-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Cocina'),
 'PC','Dell','OptiPlex 3080','COC-PC-002','Operativo','COC-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Cocina'),
 'Impresora','Epson','L3250','COC-IMP-001','Operativo','COC-INV-101'),

-- Legales (3 PCs, 2 impresoras)
((SELECT idDependencia FROM Dependencia WHERE nombre='Legales'),
 'PC','Lenovo','ThinkCentre M720','LEG-PC-001','Operativo','LEG-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Legales'),
 'PC','Lenovo','ThinkCentre M720','LEG-PC-002','Operativo','LEG-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Legales'),
 'PC','HP','EliteDesk 800','LEG-PC-003','Operativo','LEG-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Legales'),
 'Impresora','HP','LaserJet Pro M203','LEG-IMP-001','Operativo','LEG-INV-101'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Legales'),
 'Impresora','Brother','DCP-L2550DW','LEG-IMP-002','Operativo','LEG-INV-102'),

-- Direccion (3 PCs, 1 impresora)
((SELECT idDependencia FROM Dependencia WHERE nombre='Direccion'),
 'PC','Dell','XPS 8940','DIR-PC-001','Operativo','DIR-INV-001'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Direccion'),
 'PC','Dell','XPS 8940','DIR-PC-002','Operativo','DIR-INV-002'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Direccion'),
 'PC','HP','EliteDesk 800','DIR-PC-003','Operativo','DIR-INV-003'),
((SELECT idDependencia FROM Dependencia WHERE nombre='Direccion'),
 'Impresora','HP','LaserJet Pro M404','DIR-IMP-001','Operativo','DIR-INV-101');