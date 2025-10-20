package service;

import domain.model.*;
import domain.repo.DependenciaRepository;
import domain.repo.EquipoRepository;

import java.util.List;

public class EquipoService {
    private final EquipoRepository equipoRepo;
    private final DependenciaRepository depRepo;

    public EquipoService(EquipoRepository equipoRepo, DependenciaRepository depRepo){
        this.equipoRepo = equipoRepo; this.depRepo = depRepo;
    }

    public Equipo guardar(Equipo e){
        if (e.getCodigo()==null || e.getCodigo().isBlank())
            throw new IllegalArgumentException("Código de inventario obligatorio");
        if (e.getNumeroSerie()==null || e.getNumeroSerie().isBlank())
            throw new IllegalArgumentException("Número de serie obligatorio");
        if (e.getTipo()==null)
            throw new IllegalArgumentException("Tipo de equipo obligatorio");
        if (e.getDependenciaId()==null || depRepo.findById(e.getDependenciaId()).isEmpty())
            throw new IllegalArgumentException("Dependencia inexistente");

        // Unicidad de código y Nº de serie
        equipoRepo.findByCodigo(e.getCodigo()).ifPresent(ex -> {
            if (e.getId()==null || !ex.getId().equals(e.getId()))
                throw new IllegalArgumentException("El código de inventario ya existe");
        });
        equipoRepo.findByNumeroSerie(e.getNumeroSerie()).ifPresent(ex -> {
            if (e.getId()==null || !ex.getId().equals(e.getId()))
                throw new IllegalArgumentException("El número de serie ya existe");
        });

        if (e.getEstado()==null) e.setEstado(EstadoEquipo.OPERATIVO);
        return equipoRepo.save(e);
    }

    public List<Equipo> listar(){ return equipoRepo.findAll(); }
    public List<Equipo> buscarTexto(String q){ return equipoRepo.findByTexto(q); }
    public List<Equipo> listarPorDependencia(Long depId){ return equipoRepo.findByDependencia(depId); }
    public List<Equipo> listarPorTipo(TipoEquipo t){ return equipoRepo.findByTipo(t); }
    public List<Equipo> listarPorEstado(EstadoEquipo est){ return equipoRepo.findByEstado(est); }
    public boolean eliminar(Long id){ return equipoRepo.deleteById(id); }
}
