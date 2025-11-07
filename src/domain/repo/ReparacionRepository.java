package domain.repo;

import domain.model.Reparacion;
import java.util.List;
import java.util.Optional;

public interface ReparacionRepository {
    Reparacion save(Reparacion r);
    Optional<Reparacion> findById(Long id);
    List<Reparacion> findAll();
    boolean deleteById(Long id);
    List<Reparacion> findByEquipo(Long equipoId);
    List<Reparacion> findAbiertas();   // <- ESTE método debe existir
}
