package domain.model;

public class Equipo {
    private Long id;
    private String codigo;           // código inventario
    private String numeroSerie;      // único
    private TipoEquipo tipo;         // PC/IMPRESORA/ESCANER/MONITOR
    private String marca;
    private String modelo;
    private Long dependenciaId;      // FK (InMemory por id)
    private EstadoEquipo estado = EstadoEquipo.OPERATIVO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public TipoEquipo getTipo() { return tipo; }
    public void setTipo(TipoEquipo tipo) { this.tipo = tipo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Long getDependenciaId() { return dependenciaId; }
    public void setDependenciaId(Long dependenciaId) { this.dependenciaId = dependenciaId; }
    public EstadoEquipo getEstado() { return estado; }
    public void setEstado(EstadoEquipo estado) { this.estado = estado; }
}
