package domain.model;

import java.time.LocalDateTime;

public class AuditEntry {
    private LocalDateTime ts = LocalDateTime.now();
    private String operador;
    private String accion;
    private String detalle;

    public AuditEntry(String operador, String accion, String detalle){
        this.operador = operador; this.accion = accion; this.detalle = detalle;
    }
    public LocalDateTime getTs(){ return ts; }
    public String getOperador(){ return operador; }
    public String getAccion(){ return accion; }
    public String getDetalle(){ return detalle; }
}
