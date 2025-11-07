package service;

import domain.model.*;
import domain.repo.EquipoRepository;
import domain.repo.ReparacionRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReparacionService {
    private final ReparacionRepository repRepo;
    private final EquipoRepository eqRepo;

    public ReparacionService(ReparacionRepository repRepo, EquipoRepository eqRepo){
        this.repRepo = repRepo; this.eqRepo = eqRepo;
    }

    public Reparacion abrir(Reparacion r){
        var eq = eqRepo.findById(r.getEquipoId())
                .orElseThrow(() -> new IllegalArgumentException("Equipo inexistente"));
        if (eq.getEstado()==EstadoEquipo.BAJA)
            throw new IllegalStateException("No se puede abrir reparaciOn de un equipo en BAJA");
        r.setEstado(EstadoReparacion.ABIERTA);
        r.setFechaApertura(LocalDateTime.now());
        var saved = repRepo.save(r);

        eq.setEstado(EstadoEquipo.EN_REPARACION);
        eqRepo.save(eq);
        return saved;
    }

    public Reparacion cerrar(Long reparacionId, String descripcionCierre){
        var rep = repRepo.findById(reparacionId)
                .orElseThrow(() -> new IllegalArgumentException("ReparaciOn inexistente"));
        if (rep.getEstado()==EstadoReparacion.CERRADA)
            throw new IllegalStateException("La reparaciOn ya estA cerrada");
        rep.setDescripcion(descripcionCierre); // si querés guardar resultado aquí
        rep.setEstado(EstadoReparacion.CERRADA);
        rep.setFechaCierre(LocalDateTime.now());
        repRepo.save(rep);

        var eq = eqRepo.findById(rep.getEquipoId()).orElseThrow();
        if (eq.getEstado()==EstadoEquipo.EN_REPARACION){
            eq.setEstado(EstadoEquipo.OPERATIVO);
            eqRepo.save(eq);
        }
        return rep;
    }

    public List<Reparacion> listar(){ return repRepo.findAll(); }
    public List<Reparacion> abiertas(){ return repRepo.findAbiertas(); }
    public boolean eliminar(Long id){ return repRepo.deleteById(id); }
}
