package ui.equipo;

import app.Session;
import domain.model.AuditEntry;
import domain.model.Dependencia;
import domain.model.Role;
import domain.model.TipoEquipo;
import service.AuditService;
import service.DependenciaService;
import service.EquipoService;
import util.CsvExporter;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipoController {
    private final EquipoService service;
    private final DependenciaService depService;
    private final AuditService audit;
    private final JTable table;
    private final EquipoTableModel tm;
    private final EquipoForm form;

    public EquipoController(EquipoService service, DependenciaService depService,
                            AuditService audit, EquipoForm form, EquipoTableModel tm, JTable table){
        this.service = service;
        this.depService = depService;
        this.audit = audit;
        this.form = form;
        this.tm = tm;
        this.table = table;

        cargarDependencias(form, tm);
        tm.setData(service.listar());
        table.setModel(tm);

        // ==== Acciones ====
        form.onGuardar(() -> {
            try {
                var e = service.guardar(form.toModel());
                tm.setData(service.listar());
                form.clearAndFocus();
                audit.log(new AuditEntry(Session.operador(), "Guardar Equipo",
                        "ID=" + e.getId() + " COD=" + e.getCodigo()));
                JOptionPane.showMessageDialog(form, "Guardado", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form, ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        form.onEliminar(id -> {
            try {
                service.eliminar(id);
                tm.setData(service.listar());
                form.clearAndFocus();
                audit.log(new AuditEntry(Session.operador(), "Eliminar Equipo", "ID=" + id));
                JOptionPane.showMessageDialog(form, "Equipo eliminado.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                // Puede venir desde EquipoService o desde MySqlEquipoRepository
                JOptionPane.showMessageDialog(form, ex.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        form.onBuscar(q -> tm.setData(service.buscarTexto(q)));

        form.onFiltrar(() -> {
            Long depId = form.getFiltroDepId();
            TipoEquipo t = form.getFiltroTipo();

            var data = service.listar(); // base

            if (depId != null) {
                data = service.listarPorDependencia(depId);
            }

            if (t != null) {
                var porTipo = service.listarPorTipo(t);
                if (depId != null) {
                    var ids = porTipo.stream()
                            .map(x -> x.getId())
                            .collect(java.util.stream.Collectors.toSet());
                    data = data.stream()
                            .filter(x -> ids.contains(x.getId()))
                            .toList();
                } else {
                    data = porTipo;
                }
            }

            tm.setData(data);
        });

        form.onLimpiar(() -> tm.setData(service.listar()));

        form.onExportar(() -> {
            try {
                var file = chooseCsvFile(form, "stock");
                if (file != null) {
                    CsvExporter.exportTable(table, file.getAbsolutePath());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form,
                        "No se pudo exportar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                form.fromModel(tm.getAt(row));
            }
        });

        aplicarPermisosPorRol();
    }

    private void cargarDependencias(EquipoForm form, EquipoTableModel tm){
        List<Dependencia> deps = depService.listar();
        var opciones = deps.stream()
                .map(d -> new DepOpcion(d.getId(), d.getNombre()))
                .toList();
        form.setDependencias(opciones);

        Map<Long, String> depNames = new HashMap<>();
        for (Dependencia d : deps) depNames.put(d.getId(), d.getNombre());
        tm.setDepNames(depNames);
    }

    private void aplicarPermisosPorRol(){
        Role r = Session.role();
        boolean puedeEditar = (r == Role.EDICION || r == Role.ADMIN);
        boolean puedeBorrar = (r == Role.ADMIN);

       
        JOptionPane.showMessageDialog(form,
                "Rol activo: " + r +
                        (puedeEditar ? " (puede editar)" : " (solo lectura)") +
                        (puedeBorrar ? ", puede borrar" : ""),
                "Permisos", JOptionPane.INFORMATION_MESSAGE);
    }

    private File chooseCsvFile(JComponent parent, String base){
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(base + ".csv"));
        int r = fc.showSaveDialog(parent);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv")) {
                f = new File(f.getAbsolutePath() + ".csv");
            }
            return f;
        }
        return null;
    }
}
