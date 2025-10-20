package domain.model;

import java.time.LocalDateTime;

public class Reparacion {
    private Long id;
    private Long equipoId;
    private String descripcion;           // descripción de la falla / intervención
    private String tecnicoResponsable;    // técnico responsable
    private EstadoReparacion estado = EstadoReparacion.ABIERTA;
    private LocalDateTime fechaApertura = LocalDateTime.now();
    private LocalDateTime fechaCierre;    // null si no cerrada

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTecnicoResponsable() { return tecnicoResponsable; }
    public void setTecnicoResponsable(String tecnicoResponsable) { this.tecnicoResponsable = tecnicoResponsable; }
    public EstadoReparacion getEstado() { return estado; }
    public void setEstado(EstadoReparacion estado) { this.estado = estado; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
}
