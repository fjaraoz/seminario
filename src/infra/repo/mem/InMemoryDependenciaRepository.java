package infra.repo.mem;

import domain.model.Dependencia;
import domain.repo.DependenciaRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryDependenciaRepository implements DependenciaRepository {
    private final Map<Long, Dependencia> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override public Dependencia save(Dependencia d){
        if (d.getId()==null) d.setId(seq.incrementAndGet());
        data.put(d.getId(), copy(d));
        return d;
    }
    @Override public Optional<Dependencia> findById(Long id){
        return Optional.ofNullable(copy(data.get(id)));
    }
    @Override public Optional<Dependencia> findByNombre(String nombre){
        if (nombre==null) return Optional.empty();
        String n = nombre.trim().toLowerCase();
        return data.values().stream()
                .filter(x -> x.getNombre()!=null && x.getNombre().trim().toLowerCase().equals(n))
                .findFirst().map(this::copy);
    }
    @Override public List<Dependencia> findAll(){
        List<Dependencia> out = new ArrayList<>();
        for (Dependencia d : data.values()) out.add(copy(d));
        out.sort(Comparator.comparing(Dependencia::getId));
        return out;
    }
    @Override public boolean deleteById(Long id){ return data.remove(id)!=null; }

    private Dependencia copy(Dependencia d){
        if (d==null) return null;
        Dependencia c = new Dependencia();
        c.setId(d.getId()); c.setNombre(d.getNombre()); c.setResponsable(d.getResponsable());
        return c;
    }
}
