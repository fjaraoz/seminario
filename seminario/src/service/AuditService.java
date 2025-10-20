package service;

import domain.model.AuditEntry;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditService {
    private final List<AuditEntry> entries = new ArrayList<>();
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(AuditEntry e){ entries.add(e); }
    public List<AuditEntry> listar(){ return Collections.unmodifiableList(entries); }

    public String toText(){
        StringBuilder sb = new StringBuilder();
        for (var e : entries){
            sb.append(e.getTs().format(FMT)).append(" | ")
              .append(e.getOperador()).append(" | ")
              .append(e.getAccion()).append(" | ")
              .append(e.getDetalle()==null?"":e.getDetalle())
              .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
