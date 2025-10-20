package ui.equipo;

import domain.model.Dependencia;
import service.DependenciaService;
import service.EquipoService;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipoController {
    private final EquipoService service;
    private final DependenciaService depService;

    public EquipoController(EquipoService service,
                            DependenciaService depService,
                            EquipoForm form,
                            EquipoTableModel tm,
                            JTable table){
        this.service = service;
        this.depService = depService;

        // 1) Cargar dependencias en combo y nombres en tabla
        cargarDependencias(form, tm);

        // 2) Tabla inicial
        tm.setData(service.listar());
        table.setModel(tm);

        // 3) Acciones
        form.onGuardar(() -> {
            try {
                service.guardar(form.toModel());
                tm.setData(service.listar());
                form.clearAndFocus();
                form.showInfo("Guardado");
            } catch (Exception ex){ form.showError(ex.getMessage()); }
        });

        form.onEliminar(id -> {
            if (service.eliminar(id)) {
                tm.setData(service.listar());
                form.clearAndFocus();
            } else form.showWarn("No se encontró el ID");
        });

        form.onBuscar(q -> tm.setData(service.buscarTexto(q)));

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row>=0) form.fromModel(tm.getAt(row));
        });
    }

    private void cargarDependencias(EquipoForm form, EquipoTableModel tm){
        List<Dependencia> deps = depService.listar();
        // a) Combo
        var opciones = deps.stream()
                .map(d -> new DepOpcion(d.getId(), d.getNombre()))
                .toList();
        form.setDependencias(opciones);

        // b) Nombres para la tabla
        Map<Long,String> depNames = new HashMap<>();
        for (Dependencia d : deps) depNames.put(d.getId(), d.getNombre());
        tm.setDepNames(depNames);
    }
}
