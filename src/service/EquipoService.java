package service;

import domain.model.Dependencia;
import domain.model.Equipo;
import domain.model.EstadoEquipo;
import domain.model.TipoEquipo;
import domain.repo.DependenciaRepository;
import domain.repo.EquipoRepository;

import java.util.List;

public class EquipoService {
    private final EquipoRepository equipoRepo;
    private final DependenciaRepository depRepo;

    public EquipoService(EquipoRepository equipoRepo, DependenciaRepository depRepo){
        this.equipoRepo = equipoRepo;
        this.depRepo = depRepo;
    }

    public Equipo guardar(Equipo e){
        if (e == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo.");
        }

        // Dependencia obligatoria y debe existir
        if (e.getDependenciaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una dependencia.");
        }
        Dependencia dep = depRepo.findById(e.getDependenciaId())
                .orElseThrow(() -> new IllegalArgumentException("La dependencia seleccionada no existe."));

        // Tipo obligatorio
        if (e.getTipo() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de equipo.");
        }

        // Marca y modelo (podés relajar esto si querés)
        if (e.getMarca() == null || e.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("La marca es obligatoria.");
        }
        if (e.getModelo() == null || e.getModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo es obligatorio.");
        }

        // Número de serie obligatorio y único
        String numeroSerie = e.getNumeroSerie();
        if (numeroSerie == null || numeroSerie.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de serie es obligatorio.");
        }
        numeroSerie = numeroSerie.trim();
        e.setNumeroSerie(numeroSerie);

        equipoRepo.findByNumeroSerie(numeroSerie).ifPresent(otro -> {
            if (e.getId() == null || !e.getId().equals(otro.getId())) {
                throw new IllegalArgumentException("El número de serie ya existe.");
            }
        });

        // Código de inventario OPCIONAL
        String codigo = e.getCodigo();
        if (codigo != null) {
            codigo = codigo.trim();
            if (codigo.isEmpty()) {
                codigo = null;
            }
        }
        e.setCodigo(codigo);

        // Solo controlo duplicados si el código fue ingresado
        if (codigo != null) {
            equipoRepo.findByCodigo(codigo).ifPresent(otro -> {
                if (e.getId() == null || !e.getId().equals(otro.getId())) {
                    throw new IllegalArgumentException("Ya existe un equipo con ese código de inventario.");
                }
            });
        }

        // Estado por defecto
        if (e.getEstado() == null) {
            e.setEstado(EstadoEquipo.OPERATIVO);
        }

        return equipoRepo.save(e);
    }

    public List<Equipo> listar() {
        return equipoRepo.findAll();
    }

    public List<Equipo> buscarTexto(String q) {
        return equipoRepo.findByTexto(q);
    }

    public List<Equipo> listarPorDependencia(Long depId) {
        return equipoRepo.findByDependencia(depId);
    }

    public List<Equipo> listarPorTipo(TipoEquipo t) {
        return equipoRepo.findByTipo(t);
    }

    public List<Equipo> listarPorEstado(EstadoEquipo est) {
        return equipoRepo.findByEstado(est);
    }

    public boolean eliminar(Long id) {
        boolean ok = equipoRepo.deleteById(id);
        if (!ok) {
            // Podés ajustar el texto si querés algo más específico
            throw new IllegalStateException(
                    "No se pudo eliminar el equipo. Verifique que exista y que no tenga reparaciones asociadas.");
        }
        return true;
    }
}
