package ui.reparacion;

import ui.equipo.DepOpcion;
import domain.model.Reparacion;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class ReparacionForm extends JPanel {
    private final JComboBox<DepOpcion> cbEquipo = new JComboBox<>();
    private final JTextArea txtDescripcion = new JTextArea(3, 30);
    private final JTextField txtTecnico = new JTextField(18);

    private final JButton btnAbrir = new JButton("Abrir reparación");
    private final JButton btnCerrar = new JButton("Cerrar reparación");
    private final JButton btnRefrescar = new JButton("Refrescar");
    private final JButton btnExportar = new JButton("Exportar CSV");

    private Consumer<Reparacion> onAbrir;
    private LongConsumer onCerrar;
    private Runnable onRefrescar;
    private Runnable onExportar;

    public ReparacionForm(){
        setLayout(new BorderLayout(6,6));
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        p.add(new JLabel("Equipo (Código – Nº Serie):"));
        p.add(cbEquipo);
        p.add(new JLabel("Descripción:"));
        p.add(new JScrollPane(txtDescripcion));
        p.add(new JLabel("Técnico responsable:"));
        p.add(txtTecnico);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnAbrir);
        botones.add(btnCerrar);
        botones.add(btnRefrescar);
        botones.add(btnExportar);

        add(p, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        btnAbrir.addActionListener(e -> {
            if (onAbrir!=null) {
                Reparacion r = new Reparacion();
                DepOpcion op = (DepOpcion) cbEquipo.getSelectedItem();
                r.setEquipoId(op==null? null : op.getId());
                r.setDescripcion(txtDescripcion.getText());
                r.setTecnicoResponsable(txtTecnico.getText());
                onAbrir.accept(r);
            }
        });

        btnCerrar.addActionListener(e -> {
            if (onCerrar!=null) {
                String id = JOptionPane.showInputDialog(this,"ID de reparación a cerrar:");
                if (id!=null && !id.isBlank()) onCerrar.accept(Long.parseLong(id));
            }
        });

        btnRefrescar.addActionListener(e -> { if (onRefrescar!=null) onRefrescar.run(); });
        btnExportar.addActionListener(e -> { if (onExportar!=null) onExportar.run(); });
    }

    /* Hooks para el controller */
    public void onAbrir(Consumer<Reparacion> c){ this.onAbrir=c; }
    public void onCerrar(LongConsumer c){ this.onCerrar=c; }
    public void onRefrescar(Runnable r){ this.onRefrescar=r; }
    public void onExportar(Runnable r){ this.onExportar=r; }

    /* Carga el combo de equipos */
    public void setEquipos(List<DepOpcion> equipos){
        DefaultComboBoxModel<DepOpcion> m = new DefaultComboBoxModel<>();
        for (DepOpcion d : equipos) m.addElement(d);
        cbEquipo.setModel(m);
        if (m.getSize()>0) cbEquipo.setSelectedIndex(0);
    }

    /* Limpia los campos */
    public void clear(){
        txtDescripcion.setText(""); txtTecnico.setText("");
        if (cbEquipo.getItemCount()>0) cbEquipo.setSelectedIndex(0);
    }
}
