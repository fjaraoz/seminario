package domain.repo;

import domain.model.Dependencia;
import java.util.List;
import java.util.Optional;

public interface DependenciaRepository {
    Dependencia save(Dependencia d);
    Optional<Dependencia> findById(Long id);
    Optional<Dependencia> findByNombre(String nombre);
    List<Dependencia> findAll();
    boolean deleteById(Long id);
}
