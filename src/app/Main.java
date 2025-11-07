package app;

import domain.model.Dependencia;
import domain.model.Role;
import infra.repo.mem.InMemoryDependenciaRepository;
import infra.repo.mem.InMemoryEquipoRepository;
import infra.repo.mem.InMemoryReparacionRepository;
import service.AuditService;
import service.DependenciaService;
import service.EquipoService;
import service.ReparacionService;
import ui.common.AuditPanel;
import ui.equipo.EquipoController;
import ui.equipo.EquipoForm;
import ui.equipo.EquipoTableModel;
import ui.reparacion.ReparacionController;
import ui.reparacion.ReparacionForm;
import ui.reparacion.ReparacionTableModel;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            // ==== Login mínimo (operador + rol) ====
            String operador = JOptionPane.showInputDialog(null,"Operador:");
            if (operador==null || operador.isBlank()) operador = "anon";
            Object rolSel = JOptionPane.showInputDialog(
                    null, "Rol:", "Inicio",
                    JOptionPane.QUESTION_MESSAGE, null,
                    Role.values(), Role.LECTURA
            );
            Role role = rolSel==null? Role.LECTURA : (Role) rolSel;
            Session.start(operador, role);

          // ==== Repos/Servicios ====
boolean usarMySql = true; // ponelo en true para TP4 (MySQL), false para modo InMemory anterior

domain.repo.DependenciaRepository depRepo;
domain.repo.EquipoRepository eqRepo;
domain.repo.ReparacionRepository repRepo;

if (usarMySql) {
    depRepo = new infra.repo.mysql.MySqlDependenciaRepository();
    eqRepo  = new infra.repo.mysql.MySqlEquipoRepository();
    repRepo = new infra.repo.mysql.MySqlReparacionRepository();
} else {
    depRepo = new infra.repo.mem.InMemoryDependenciaRepository();
    eqRepo  = new infra.repo.mem.InMemoryEquipoRepository();
    repRepo = new infra.repo.mem.InMemoryReparacionRepository();
}

var depSrv = new service.DependenciaService(depRepo);
var eqSrv  = new service.EquipoService(eqRepo, depRepo);
var repSrv = new service.ReparacionService(repRepo, eqRepo);
var audit  = new service.AuditService();


            // ==== Seed dependencias para probar combos ====
           // var d1 = new Dependencia(); d1.setNombre("Mesa de Entradas"); d1.setResponsable("Ana");
           // var d2 = new Dependencia(); d2.setNombre("Recursos Humanos"); d2.setResponsable("Luis");
            //depSrv.guardar(d1); depSrv.guardar(d2);

            // ==== UI ====
            var frame = new JFrame("Inventario y Reparaciones - TP3 (InMemory)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Tab 1: Equipos
            var eqForm = new EquipoForm();
            var eqTable = new JTable();
            var eqTM = new EquipoTableModel();
            new EquipoController(eqSrv, depSrv, audit, eqForm, eqTM, eqTable);
            var panelEquipos = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    new JScrollPane(eqTable), eqForm);
            panelEquipos.setDividerLocation(300);

            // Tab 2: Reparaciones
            var repForm = new ReparacionForm();
            var repTable = new JTable();
            var repTM = new ReparacionTableModel();
            new ReparacionController(repSrv, eqSrv, audit, repForm, repTM, repTable);
            var panelRep = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    new JScrollPane(repTable), repForm);
            panelRep.setDividerLocation(260);

            // Tab 3: Auditoría
            var panelAudit = new AuditPanel(audit);

            var tabs = new JTabbedPane();
            tabs.addTab("Equipos", panelEquipos);
            tabs.addTab("Reparaciones", panelRep);
            tabs.addTab("Auditoría", panelAudit);

            frame.setContentPane(tabs);
            frame.setSize(1150, 760);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
