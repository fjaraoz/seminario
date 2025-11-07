package service;

import domain.model.Dependencia;
import domain.repo.DependenciaRepository;
import java.util.List;

public class DependenciaService {
    private final DependenciaRepository repo;
    public DependenciaService(DependenciaRepository repo){ this.repo = repo; }

    public Dependencia guardar(Dependencia d){
        if (d.getNombre()==null || d.getNombre().isBlank())
            throw new IllegalArgumentException("Nombre de dependencia obligatorio");
        repo.findByNombre(d.getNombre()).ifPresent(ex -> {
            if (d.getId()==null || !ex.getId().equals(d.getId()))
                throw new IllegalArgumentException("La dependencia ya existe");
        });
        return repo.save(d);
    }
    public List<Dependencia> listar(){ return repo.findAll(); }
}
