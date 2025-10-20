-- 5.1) Stock por dependencia (con totales)
SELECT d.sigla AS dependencia,
       e.tipo,
       e.estado,
       COUNT(*) AS cantidad
FROM Equipo e
JOIN Dependencia d ON d.idDependencia = e.idDependencia
GROUP BY d.sigla, e.tipo, e.estado
ORDER BY d.sigla, e.tipo, e.estado;


-- 5.2) Búsqueda filtrada (por dependencia y tipo)
SELECT e.idEquipo, d.sigla AS dep, e.tipo, e.marca, e.modelo, e.nroSerie, e.estado
FROM Equipo e
JOIN Dependencia d ON d.idDependencia = e.idDependencia
WHERE d.sigla = 'INF' AND e.tipo IN ('PC','Impresora')
ORDER BY e.tipo, e.marca, e.modelo;

-- 5.3) Reparaciones en período (con técnico)
SELECT r.idReparacion, e.nroSerie, r.fechaApertura, r.estado,
       COALESCE(u.nombre,'(sin asignar)') AS tecnico
FROM Reparacion r
JOIN Equipo e   ON e.idEquipo  = r.idEquipo
LEFT JOIN Usuario u ON u.idUsuario = r.idTecnico
WHERE r.fechaApertura BETWEEN DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND CURDATE()
ORDER BY r.fechaApertura DESC;

-- 5.4) Última reparación por equipo (si existe)
SELECT e.nroSerie,
       r.idReparacion,
       r.estado,
       r.fechaApertura,
       r.fechaCierre
FROM Equipo e
LEFT JOIN Reparacion r
  ON r.idReparacion = (
      SELECT r2.idReparacion
      FROM Reparacion r2
      WHERE r2.idEquipo = e.idEquipo
      ORDER BY r2.fechaApertura DESC, r2.idReparacion DESC
      LIMIT 1
  )
ORDER BY e.nroSerie;

-- 5.5) Métricas (agregados)
-- Equipos por tipo (totales)
SELECT tipo, COUNT(*) AS cantidad
FROM Equipo
GROUP BY tipo
ORDER BY cantidad DESC;

-- Reparaciones por estado (últimos 30 días)
SELECT estado, COUNT(*) AS cantidad
FROM Reparacion
WHERE fechaApertura >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY estado
ORDER BY cantidad DESC;