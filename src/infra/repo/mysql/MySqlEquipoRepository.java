package infra.repo.mysql;

import domain.model.Equipo;
import domain.model.EstadoEquipo;
import domain.model.TipoEquipo;
import domain.repo.EquipoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlEquipoRepository implements EquipoRepository {

    private String toDbTipo(TipoEquipo t) {
        if (t == null) return "PC";
        return switch (t) {
            case PC -> "PC";
            case IMPRESORA -> "Impresora";
            case ESCANER -> "Escaner";
            case MONITOR -> "Monitor";
        };
    }

    private TipoEquipo toDomainTipo(String db) {
        if (db == null) return null;
        return switch (db) {
            case "PC" -> TipoEquipo.PC;
            case "Impresora" -> TipoEquipo.IMPRESORA;
            case "Escaner" -> TipoEquipo.ESCANER;
            case "Monitor" -> TipoEquipo.MONITOR;
            default -> null;
        };
    }

    private String toDbEstado(EstadoEquipo e) {
        if (e == null) return "Operativo";
        return switch (e) {
            case OPERATIVO -> "Operativo";
            case EN_REPARACION -> "EnReparacion";
            case BAJA -> "Baja";
        };
    }

    private EstadoEquipo toDomainEstado(String db) {
        if (db == null) return EstadoEquipo.OPERATIVO;
        return switch (db) {
            case "Operativo" -> EstadoEquipo.OPERATIVO;
            case "EnReparacion" -> EstadoEquipo.EN_REPARACION;
            case "Baja" -> EstadoEquipo.BAJA;
            default -> EstadoEquipo.OPERATIVO;
        };
    }

    private Equipo mapRow(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getLong("idEquipo"));
        e.setDependenciaId(rs.getLong("idDependencia"));
        e.setTipo(toDomainTipo(rs.getString("tipo")));
        e.setMarca(rs.getString("marca"));
        e.setModelo(rs.getString("modelo"));
        e.setNumeroSerie(rs.getString("nroSerie"));
        e.setEstado(toDomainEstado(rs.getString("estado")));
        // usamos "observaciones" para guardar el código de inventario
        e.setCodigo(rs.getString("observaciones"));
        return e;
    }

    @Override
    public Equipo save(Equipo e) {
        try (Connection cn = MySqlConnectionProvider.getConnection()) {
            if (e.getId() == null) {
                String sql = "INSERT INTO Equipo " +
                        "(idDependencia, tipo, marca, modelo, nroSerie, estado, observaciones) " +
                        "VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, e.getDependenciaId());
                    ps.setString(2, toDbTipo(e.getTipo()));
                    ps.setString(3, e.getMarca());
                    ps.setString(4, e.getModelo());
                    ps.setString(5, e.getNumeroSerie());
                    ps.setString(6, toDbEstado(e.getEstado()));
                    ps.setString(7, e.getCodigo());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) e.setId(keys.getLong(1));
                    }
                }
            } else {
                String sql = "UPDATE Equipo " +
                        "SET idDependencia=?, tipo=?, marca=?, modelo=?, nroSerie=?, estado=?, observaciones=? " +
                        "WHERE idEquipo=?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setLong(1, e.getDependenciaId());
                    ps.setString(2, toDbTipo(e.getTipo()));
                    ps.setString(3, e.getMarca());
                    ps.setString(4, e.getModelo());
                    ps.setString(5, e.getNumeroSerie());
                    ps.setString(6, toDbEstado(e.getEstado()));
                    ps.setString(7, e.getCodigo());
                    ps.setLong(8, e.getId());
                    ps.executeUpdate();
                }
            }
            return e;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al guardar Equipo en MySQL", ex);
        }
    }

    @Override
    public Optional<Equipo> findById(Long id) {
        String sql = "SELECT * FROM Equipo WHERE idEquipo=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipo por id", ex);
        }
    }

    @Override
    public List<Equipo> findAll() {
        String sql = "SELECT * FROM Equipo ORDER BY idEquipo";
        List<Equipo> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar Equipos", ex);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Equipo WHERE idEquipo=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // FK con Reparacion
            throw new RuntimeException(
                    "No se puede eliminar el equipo porque tiene reparaciones asociadas.", e);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar Equipo", ex);
        }
    }

    @Override
    public Optional<Equipo> findByCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        String sql = "SELECT * FROM Equipo WHERE observaciones = ?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipo por código", ex);
        }
    }

    @Override
    public Optional<Equipo> findByNumeroSerie(String numeroSerie) {
        if (numeroSerie == null) return Optional.empty();
        String sql = "SELECT * FROM Equipo WHERE LOWER(nroSerie) = LOWER(?)";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numeroSerie);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipo por número de serie", ex);
        }
    }

    @Override
    public List<Equipo> findByTexto(String t) {
        String q = t == null ? "" : t.trim().toLowerCase();
        String sql = """
                SELECT * FROM Equipo
                WHERE LOWER(CONCAT_WS(' ', COALESCE(observaciones,''), COALESCE(nroSerie,''),
                                      COALESCE(marca,''), COALESCE(modelo,'')))
                      LIKE ?
                ORDER BY idEquipo
                """;
        List<Equipo> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipos por texto", ex);
        }
    }

    @Override
    public List<Equipo> findByDependencia(Long dependenciaId) {
        String sql = "SELECT * FROM Equipo WHERE idDependencia=? ORDER BY idEquipo";
        List<Equipo> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, dependenciaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipos por dependencia", ex);
        }
    }

    @Override
    public List<Equipo> findByTipo(TipoEquipo tipo) {
        String sql = "SELECT * FROM Equipo WHERE tipo=? ORDER BY idEquipo";
        List<Equipo> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, toDbTipo(tipo));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipos por tipo", ex);
        }
    }

    @Override
    public List<Equipo> findByEstado(EstadoEquipo estado) {
        String sql = "SELECT * FROM Equipo WHERE estado=? ORDER BY idEquipo";
        List<Equipo> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, toDbEstado(estado));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar Equipos por estado", ex);
        }
    }
}
