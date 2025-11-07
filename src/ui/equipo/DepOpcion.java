package ui.equipo;

public class DepOpcion {
    private final Long id;
    private final String nombre;

    public DepOpcion(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }

    @Override public String toString() {
        return nombre == null ? "(sin nombre)" : nombre;
    }
}
