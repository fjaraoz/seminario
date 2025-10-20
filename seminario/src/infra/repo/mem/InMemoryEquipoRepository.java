package infra.repo.mem;

import domain.model.Equipo;
import domain.model.EstadoEquipo;
import domain.model.TipoEquipo;
import domain.repo.EquipoRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEquipoRepository implements EquipoRepository {
    private final Map<Long, Equipo> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override public Equipo save(Equipo e){
        if (e.getId()==null) e.setId(seq.incrementAndGet());
        data.put(e.getId(), copy(e));
        return e;
    }
    @Override public Optional<Equipo> findById(Long id){
        return Optional.ofNullable(copy(data.get(id)));
    }
    @Override public List<Equipo> findAll(){
        List<Equipo> out = new ArrayList<>();
        for (Equipo e : data.values()) out.add(copy(e));
        out.sort(Comparator.comparing(Equipo::getId));
        return out;
    }
    @Override public boolean deleteById(Long id){ return data.remove(id)!=null; }

    @Override public Optional<Equipo> findByCodigo(String codigo){
        if (codigo==null) return Optional.empty();
        String k = codigo.trim().toLowerCase();
        return data.values().stream()
                .filter(e -> e.getCodigo()!=null && e.getCodigo().trim().toLowerCase().equals(k))
                .findFirst().map(this::copy);
    }
    @Override public Optional<Equipo> findByNumeroSerie(String numeroSerie){
        if (numeroSerie==null) return Optional.empty();
        String k = numeroSerie.trim().toLowerCase();
        return data.values().stream()
                .filter(e -> e.getNumeroSerie()!=null && e.getNumeroSerie().trim().toLowerCase().equals(k))
                .findFirst().map(this::copy);
    }

    @Override public List<Equipo> findByTexto(String t){
        String q = (t==null?"":t).toLowerCase();
        List<Equipo> out = new ArrayList<>();
        for (Equipo e : data.values()){
            String blob = (Objects.toString(e.getCodigo(),"")+" "+
                           Objects.toString(e.getNumeroSerie(),"")+" "+
                           Objects.toString(e.getMarca(),"")+" "+
                           Objects.toString(e.getModelo(),"")).toLowerCase();
            if (blob.contains(q)) out.add(copy(e));
        }
        out.sort(Comparator.comparing(Equipo::getId));
        return out;
    }

    @Override public List<Equipo> findByDependencia(Long depId){
        List<Equipo> out = new ArrayList<>();
        for (Equipo e : data.values()){
            if (Objects.equals(e.getDependenciaId(), depId)) out.add(copy(e));
        }
        out.sort(Comparator.comparing(Equipo::getId));
        return out;
    }
    @Override public List<Equipo> findByTipo(TipoEquipo tipo){
        List<Equipo> out = new ArrayList<>();
        for (Equipo e : data.values()){
            if (e.getTipo()==tipo) out.add(copy(e));
        }
        out.sort(Comparator.comparing(Equipo::getId));
        return out;
    }
    @Override public List<Equipo> findByEstado(EstadoEquipo estado){
        List<Equipo> out = new ArrayList<>();
        for (Equipo e : data.values()){
            if (e.getEstado()==estado) out.add(copy(e));
        }
        out.sort(Comparator.comparing(Equipo::getId));
        return out;
    }

    private Equipo copy(Equipo e){
        if (e==null) return null;
        Equipo c = new Equipo();
        c.setId(e.getId());
        c.setCodigo(e.getCodigo());
        c.setNumeroSerie(e.getNumeroSerie());
        c.setTipo(e.getTipo());
        c.setMarca(e.getMarca());
        c.setModelo(e.getModelo());
        c.setDependenciaId(e.getDependenciaId());
        c.setEstado(e.getEstado());
        return c;
    }
}
