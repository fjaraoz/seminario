package infra.repo.mysql;

import domain.model.Dependencia;
import domain.repo.DependenciaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlDependenciaRepository implements DependenciaRepository {

    private Dependencia mapRow(ResultSet rs) throws SQLException {
        Dependencia d = new Dependencia();
        d.setId(rs.getLong("idDependencia"));
        d.setNombre(rs.getString("nombre"));
        // La tabla no tiene "responsable"; lo dejamos null
        return d;
    }

    private String generarSigla(String nombre) {
        if (nombre == null || nombre.isBlank()) return "GEN";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].length() <= 5
                    ? partes[0].toUpperCase()
                    : partes[0].substring(0, 5).toUpperCase();
        }
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            sb.append(Character.toUpperCase(p.charAt(0)));
        }
        return sb.toString();
    }

    @Override
    public Dependencia save(Dependencia d) {
        try (Connection cn = MySqlConnectionProvider.getConnection()) {
            if (d.getId() == null) {
                String sql = "INSERT INTO Dependencia (nombre, sigla, ubicacion, estado) " +
                             "VALUES (?,?,?, 'Activa')";
                try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, d.getNombre());
                    ps.setString(2, generarSigla(d.getNombre()));
                    ps.setString(3, null); // ubicacion
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) d.setId(keys.getLong(1));
                    }
                }
            } else {
                String sql = "UPDATE Dependencia SET nombre=?, sigla=? WHERE idDependencia=?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, d.getNombre());
                    ps.setString(2, generarSigla(d.getNombre()));
                    ps.setLong(3, d.getId());
                    ps.executeUpdate();
                }
            }
            return d;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar Dependencia en MySQL", e);
        }
    }

    @Override
    public Optional<Dependencia> findById(Long id) {
        String sql = "SELECT idDependencia, nombre FROM Dependencia WHERE idDependencia=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Dependencia por id", e);
        }
    }

    @Override
    public Optional<Dependencia> findByNombre(String nombre) {
        String sql = "SELECT idDependencia, nombre FROM Dependencia WHERE nombre=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Dependencia por nombre", e);
        }
    }

    @Override
    public List<Dependencia> findAll() {
        String sql = "SELECT idDependencia, nombre FROM Dependencia ORDER BY nombre";
        List<Dependencia> out = new ArrayList<>();
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Dependencias", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Dependencia WHERE idDependencia=?";
        try (Connection cn = MySqlConnectionProvider.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            // Si hay FK, va a fallar; devolvemos false y dejamos el mensaje en el stack para depuración.
            return false;
        }
    }
}
