package ui.equipo;

import domain.model.Equipo;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class EquipoTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Codigo","Numero de Serie","Tipo","Marca","Modelo","Dependencia","Estado"};
    private List<Equipo> data = new ArrayList<>();
    /** Mapa para mostrar el nombre de la dependencia por id */
    private Map<Long,String> depNames = new HashMap<>();

    public void setData(List<Equipo> d){ this.data = d; fireTableDataChanged(); }
    public Equipo getAt(int row){ return data.get(row); }

    /** Carga/actualiza nombres de dependencias */
    public void setDepNames(Map<Long,String> depNames){
        this.depNames = depNames==null? new HashMap<>() : new HashMap<>(depNames);
        fireTableDataChanged();
    }

    @Override public int getRowCount(){ return data.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int c){ return cols[c]; }

    @Override public Object getValueAt(int r,int c){
        Equipo e = data.get(r);
        return switch(c){
            case 0 -> e.getId();
            case 1 -> e.getCodigo();
            case 2 -> e.getNumeroSerie();
            case 3 -> e.getTipo();
            case 4 -> e.getMarca();
            case 5 -> e.getModelo();
            case 6 -> depNames.getOrDefault(e.getDependenciaId(), ""); // nombre
            case 7 -> e.getEstado();
            default -> "";
        };
    }
}
