package infra.repo.mysql;

import domain.model.EstadoReparacion;
import domain.model.Reparacion;
import domain.repo.ReparacionRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlReparacionRepository implements ReparacionRepository {

    private String toDbEstado(EstadoReparacion e) {
        if (e == null) return "Pendiente";
        return switch (e) {
            case ABIERTA -> "Pendiente";
            case EN_PROCESO -> "EnCurso";
            case CERRADA -> "Finalizada";
        };
    }

    private EstadoReparacion toDomainEstado(String db) {
        if (db == null) return EstadoReparacion.ABIERTA;
        return switch (db) {
            case "Pendiente", "Asignada", "EnEspera" -> EstadoReparacion.ABIERTA;
            case "EnCurso" -> EstadoReparacion.EN_PROCESO;
            case "Finalizada", "NoReparable" -> EstadoReparacion.CERRADA;
            default -> EstadoReparacion.ABIERTA;
        };
    }

    private LocalDate toDate(LocalDateTime dt) {
        return dt == null ? null : dt.toLocalDate();
    }

    private Reparacion mapRow(ResultSet rs) throws SQLException {
        Reparacion r = new Reparacion();
        r.setId(rs.getLong("idReparacion"));
        r.setEquipoId(rs.getLong("idEquipo"));
        r.setDescripcion(rs.getString("descripcionFalla"));
        r.setEstado(toDomainEstado(rs.getString("estado")));

        Date fa = rs.getDate("fechaApertura");
        if (fa != null) r.setFechaApertura(fa.toLocalDate().atStartOfDay());

        Date fc = rs.getDate("fechaCierre");
        if (fc != null) r.setFechaCierre(fc.toLocalDate().atStartOfDay());

        r.setTecnicoResponsable(rs.getString("observaciones"));
        return r;
    }

    @Override
    public Reparacion save(Reparacion r) {
        try (Connection cn = MySqlConnectionProvider.getConnection()) {
            if (r.getId() == null) {
                String sql = "INSERT INTO Reparacion " +
                        "(idEquipo, idTecnico, fechaApertura, descripcionFalla, estado, fechaCierre, observaciones) " +
                        "VALUES (NULLIF(?,0), NULL, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, r.getEquipoId());
                    LocalDate fa = toDate(r.getFechaApertura());
                    ps.setDate(2, fa == null ? new Date(System.currentTimeMillis()) : Date.valueOf(fa));
                    ps.setString(3, r.getDescripcion());
                    ps.setString(4, toDbEstado(r.getEstado()));
                    LocalDate fc = toDate(r.getFechaCierre());
                    if (fc == null) ps.setNull(5, Types.DATE);
                    else ps.setDate(5, Date.valueOf(fc));
                    ps.setString(6, r.getTecnicoResponsable());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) r.setId(keys.getLong(1));
                    }
                }
            } else {
                String sql = "UPDATE Reparacion " +
                        "SET idEquipo=?, fechaApertura=?, descripcionFalla=?, estado=?, fechaCierre=?, observaciones=? " +
                        "WHERE idReparacion=?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setLong(1, r.getEquipoId());
                    LocalDate fa = toDate(r.getFechaApertura());
                    ps.setDate(2, fa == null ? new Date(System.currentTimeMillis()) : Date.valueOf(fa));
                    ps.setString(3, r.getDescripcion());
                    ps.setString(4, toDbEstado(r.getEstado()));
                    LocalDate fc = toDate(r.getFechaCierre());
                    if (fc == null) ps.setNull(5, Types.DATE);
                    else ps.setDate(5, Date.valueOf(fc));
                    ps.setString(6, r.getTecnicoResponsable());
                    ps.setLong(7, r.getId());
                    ps.executeUpdate();
                }
            }
            return r;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al guardar Reparación en MySQL", ex);
        }
    }

    @Override
    public Optional<Reparacion> findById(Long id) {
        String sql = "SELECT * FROM Reparacion WHERE idReparacion=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Reparación por id", ex);
        }
    }

    @Override
    public List<Reparacion> findAll() {
        String sql = "SELECT * FROM Reparacion ORDER BY idReparacion";
        List<Reparacion> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar Reparaciones", ex);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Reparacion WHERE idReparacion=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public List<Reparacion> findByEquipo(Long equipoId) {
        String sql = "SELECT * FROM Reparacion WHERE idEquipo=? ORDER BY idReparacion";
        List<Reparacion> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, equipoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Reparaciones por equipo", ex);
        }
    }

    @Override
    public List<Reparacion> findAbiertas() {
        String sql = "SELECT * FROM Reparacion " +
                "WHERE estado IN ('Pendiente','Asignada','EnEspera','EnCurso') ORDER BY idReparacion";
        List<Reparacion> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar Reparaciones abiertas", ex);
        }
    }
}
