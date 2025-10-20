package domain.repo;

import domain.model.Equipo;
import domain.model.EstadoEquipo;
import domain.model.TipoEquipo;

import java.util.List;
import java.util.Optional;

public interface EquipoRepository {
    Equipo save(Equipo e);
    Optional<Equipo> findById(Long id);
    List<Equipo> findAll();
    boolean deleteById(Long id);

    Optional<Equipo> findByCodigo(String codigo);
    Optional<Equipo> findByNumeroSerie(String numeroSerie);

    List<Equipo> findByTexto(String texto);
    List<Equipo> findByDependencia(Long dependenciaId);
    List<Equipo> findByTipo(TipoEquipo tipo);
    List<Equipo> findByEstado(EstadoEquipo estado);
}
