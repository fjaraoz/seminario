package ui.reparacion;

import app.Session;
import domain.model.AuditEntry;
import domain.model.Role;
import service.AuditService;
import service.EquipoService;
import service.ReparacionService;
import ui.equipo.DepOpcion;
import util.CsvExporter;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class ReparacionController {
    private final ReparacionService repSrv;
    private final EquipoService eqSrv;
    private final AuditService audit;
    private final ReparacionForm form;
    private final ReparacionTableModel tm;
    private final JTable table;

    public ReparacionController(ReparacionService repSrv,
                                EquipoService eqSrv,
                                AuditService audit,
                                ReparacionForm form,
                                ReparacionTableModel tm,
                                JTable table){
        this.repSrv = repSrv;
        this.eqSrv = eqSrv;
        this.audit = audit;
        this.form = form;
        this.tm = tm;
        this.table = table;

        // Inicializa combo y listado
        cargarEquipos();
        tm.setData(repSrv.listar());
        table.setModel(tm);

        // Abrir reparación
        form.onAbrir(r -> {
            try {
                var saved = repSrv.abrir(r);
                tm.setData(repSrv.listar());
                audit.log(new AuditEntry(Session.operador(),
                        "Abrir Reparacion","ID="+saved.getId()+" Equipo="+saved.getEquipoId()));
                form.clear();
                JOptionPane.showMessageDialog(form,"Reparacion abierta","Info",JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex){
                JOptionPane.showMessageDialog(form, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cerrar reparación (ingresando ID)
        form.onCerrar(id -> {
            try {
                var saved = repSrv.cerrar(id, "Cierre por: "+Session.operador());
                tm.setData(repSrv.listar());
                audit.log(new AuditEntry(Session.operador(),
                        "Cerrar Reparacion","ID="+saved.getId()));
            } catch (Exception ex){
                JOptionPane.showMessageDialog(form, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        // Exportar CSV
        form.onExportar(() -> {
            try {
                var file = chooseCsvFile(form, "reparaciones");
                if (file!=null) { CsvExporter.exportTable(table, file.getAbsolutePath()); }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(form,"No se pudo exportar: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        // REFRESCAR: recarga combo y listado
        form.onRefrescar(() -> {
            cargarEquipos();
            tm.setData(repSrv.listar());
        });

        // Aviso por rol (si querés, puedo deshabilitar botones según rol)
        aplicarPermisosPorRol();
    }

    /** Llena el combo con "Código – Nº Serie" de los equipos */
    private void cargarEquipos(){
        List<domain.model.Equipo> equipos = eqSrv.listar();
        var opciones = equipos.stream()
                .map(e -> new DepOpcion(
                        e.getId(),
                        (e.getCodigo()==null? "" : e.getCodigo()) + " – " +
                        (e.getNumeroSerie()==null? "" : e.getNumeroSerie())
                ))
                .toList();
        form.setEquipos(opciones);
    }

    private void aplicarPermisosPorRol(){
        Role r = Session.role();
        if (r==Role.LECTURA){
            JOptionPane.showMessageDialog(form,"Rol LECTURA: no puede abrir/cerrar",
                    "Permisos",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private File chooseCsvFile(JComponent parent, String base){
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(base+".csv"));
        int r = fc.showSaveDialog(parent);
        if (r==JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv"))
                f = new File(f.getAbsolutePath()+".csv");
            return f;
        }
        return null;
    }
}
