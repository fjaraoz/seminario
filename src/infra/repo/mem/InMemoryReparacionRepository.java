package infra.repo.mem;

import domain.model.EstadoReparacion;
import domain.model.Reparacion;
import domain.repo.ReparacionRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReparacionRepository implements ReparacionRepository {
    private final Map<Long, Reparacion> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Reparacion save(Reparacion r){
        if (r.getId()==null) r.setId(seq.incrementAndGet());
        data.put(r.getId(), copy(r));
        return r;
    }

    @Override
    public Optional<Reparacion> findById(Long id){
        return Optional.ofNullable(copy(data.get(id)));
    }

    @Override
    public List<Reparacion> findAll(){
        List<Reparacion> out = new ArrayList<>();
        for (Reparacion r : data.values()) out.add(copy(r));
        out.sort(Comparator.comparing(Reparacion::getId));
        return out;
    }

    @Override
    public boolean deleteById(Long id){
        return data.remove(id)!=null;
    }

    @Override
    public List<Reparacion> findByEquipo(Long equipoId){
        List<Reparacion> out = new ArrayList<>();
        for (Reparacion r : data.values()){
            if (Objects.equals(r.getEquipoId(), equipoId)) out.add(copy(r));
        }
        out.sort(Comparator.comparing(Reparacion::getId));
        return out;
    }

    @Override
    public List<Reparacion> findAbiertas(){
        List<Reparacion> out = new ArrayList<>();
        for (Reparacion r : data.values()){
            if (r.getEstado()== EstadoReparacion.ABIERTA ||
                r.getEstado()== EstadoReparacion.EN_PROCESO) {
                out.add(copy(r));
            }
        }
        out.sort(Comparator.comparing(Reparacion::getId));
        return out;
    }

    // ---- helper: copia defensiva ----
    private Reparacion copy(Reparacion r){
        if (r==null) return null;
        Reparacion c = new Reparacion();
        c.setId(r.getId());
        c.setEquipoId(r.getEquipoId());
        c.setDescripcion(r.getDescripcion());
        c.setTecnicoResponsable(r.getTecnicoResponsable());
        c.setEstado(r.getEstado());
        c.setFechaApertura(r.getFechaApertura());
        c.setFechaCierre(r.getFechaCierre());
        return c;
    }
}
