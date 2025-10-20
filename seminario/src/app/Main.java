package app;

import infra.repo.mem.InMemoryDependenciaRepository;
import infra.repo.mem.InMemoryEquipoRepository;
import infra.repo.mem.InMemoryReparacionRepository;
import service.DependenciaService;
import service.EquipoService;
import service.ReparacionService;
import ui.equipo.EquipoController;
import ui.equipo.EquipoForm;
import ui.equipo.EquipoTableModel;

import javax.swing.*;

import domain.model.Dependencia;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            var depRepo = new InMemoryDependenciaRepository();
            var eqRepo  = new InMemoryEquipoRepository();
            var repRepo = new InMemoryReparacionRepository();

            var depSrv = new DependenciaService(depRepo);
            var eqSrv  = new EquipoService(eqRepo, depRepo);
            var repSrv = new ReparacionService(repRepo, eqRepo); // (UI de reparaciones se puede sumar luego)

            // Seed de dependencias (mínimo para probar la combo)
            var d1 = new Dependencia(); d1.setNombre("Mesa de Entradas"); d1.setResponsable("Ana");
            var d2 = new Dependencia(); d2.setNombre("Recursos Humanos"); d2.setResponsable("Luis");
            depSrv.guardar(d1); depSrv.guardar(d2);

            var frame = new JFrame("Inventario y Reparaciones - TP3 (InMemory)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            var eqForm = new EquipoForm();
            var eqTable = new JTable();
            var eqTM = new EquipoTableModel();
            new EquipoController(eqSrv, depSrv, eqForm, eqTM, eqTable);

            var pnl = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    new JScrollPane(eqTable), eqForm);
            pnl.setDividerLocation(260);

            frame.setContentPane(pnl);
            frame.setSize(1050,650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
