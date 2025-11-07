package ui.reparacion;

import domain.model.Reparacion;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReparacionTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","EquipoId","Estado","F. Apertura","F. Cierre","DescripciOn","TEcnico"};
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private List<Reparacion> data = new ArrayList<>();

    public void setData(List<Reparacion> d){ this.data = d; fireTableDataChanged(); }
    public Reparacion getAt(int row){ return data.get(row); }

    @Override public int getRowCount(){ return data.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int c){ return cols[c]; }

    @Override public Object getValueAt(int r,int c){
        var x = data.get(r);
        return switch(c){
            case 0 -> x.getId();
            case 1 -> x.getEquipoId();
            case 2 -> x.getEstado();
            case 3 -> x.getFechaApertura()==null? "" : x.getFechaApertura().format(FMT);
            case 4 -> x.getFechaCierre()==null? "" : x.getFechaCierre().format(FMT);
            case 5 -> x.getDescripcion();
            case 6 -> x.getTecnicoResponsable();
            default -> "";
        };
    }
}
