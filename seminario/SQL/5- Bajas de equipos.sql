-- 6.1) Baja lógica de equipo (CU3)
UPDATE Equipo
SET estado = 'Baja', observaciones = CONCAT(COALESCE(observaciones,''),' | Baja ', CURDATE())
WHERE nroSerie = 'SN-MON-021';

INSERT INTO Auditoria (accion, idUsuario, tipoRecurso, idRecurso, detalleJson)
SELECT 'BajaEquipo', u.idUsuario, 'Equipo', e.idEquipo,
       JSON_OBJECT('motivo', 'Fuera de servicio', 'fecha', CURDATE())
FROM Usuario u, Equipo e
WHERE u.username='admin1' AND e.nroSerie='SN-MON-021'
LIMIT 1;

-- 6.2) DELETE seguro en tabla de unión (ejemplo)
DELETE ur
FROM UsuarioRol ur
JOIN Usuario u ON u.idUsuario = ur.idUsuario
JOIN Rol r     ON r.idRol     = ur.idRol
WHERE u.username='tecnico1' AND r.nombre='Edicion';
