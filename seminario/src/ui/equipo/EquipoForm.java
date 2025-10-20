package ui.equipo;

import domain.model.Equipo;
import domain.model.EstadoEquipo;
import domain.model.TipoEquipo;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class EquipoForm extends JPanel {
    // EDICION
    private final JTextField txtId=new JTextField(6),
            txtCodigo=new JTextField(12),
            txtNumeroSerie=new JTextField(14),
            txtMarca=new JTextField(12),
            txtModelo=new JTextField(12);
    private final JComboBox<TipoEquipo> cbTipo = new JComboBox<>(TipoEquipo.values());
    private final JComboBox<DepOpcion> cbDependencia = new JComboBox<>();
    private final JComboBox<EstadoEquipo> cbEstado=new JComboBox<>(EstadoEquipo.values());

    private final JButton btnGuardar=new JButton("Guardar"),
            btnEliminar=new JButton("Eliminar");

    // BUSQUEDA / FILTROS
    private final JTextField txtBuscar=new JTextField(18);
    private final JButton btnBuscar=new JButton("Buscar");
    private final JComboBox<TipoEquipo> cbFiltroTipo = new JComboBox<>(TipoEquipo.values());
    private final JComboBox<DepOpcion> cbFiltroDep = new JComboBox<>();
    private final JButton btnFiltrar=new JButton("Filtrar"), btnLimpiar=new JButton("Limpiar");
    private final JButton btnExportar=new JButton("Exportar CSV");

    // hooks
    private Runnable onGuardar; private LongConsumer onEliminar;
    private Consumer<String> onBuscar;
    private Runnable onFiltrar; private Runnable onLimpiar; private Runnable onExportar;

    public EquipoForm(){
        setLayout(new BorderLayout(8,8));

        JPanel ed = new JPanel(new GridLayout(0,2,6,6));
        txtId.setEditable(false);

        ed.add(new JLabel("ID:")); ed.add(txtId);
        ed.add(new JLabel("Código inventario:")); ed.add(txtCodigo);
        ed.add(new JLabel("Nº de serie:")); ed.add(txtNumeroSerie);
        ed.add(new JLabel("Tipo:")); ed.add(cbTipo);
        ed.add(new JLabel("Marca:")); ed.add(txtMarca);
        ed.add(new JLabel("Modelo:")); ed.add(txtModelo);
        ed.add(new JLabel("Dependencia:")); ed.add(cbDependencia);
        ed.add(new JLabel("Estado:")); ed.add(cbEstado);
        ed.add(btnGuardar); ed.add(btnEliminar);

        JPanel filtros = new JPanel(new GridLayout(0,2,6,6));
        filtros.setBorder(BorderFactory.createTitledBorder("Búsqueda / Filtros"));
        filtros.add(new JLabel("Buscar texto:")); filtros.add(txtBuscar);
        filtros.add(new JLabel()); filtros.add(btnBuscar);
        filtros.add(new JLabel("Por Tipo:")); filtros.add(cbFiltroTipo);
        filtros.add(new JLabel("Por Dependencia:")); filtros.add(cbFiltroDep);
        filtros.add(btnFiltrar); filtros.add(btnLimpiar);
        filtros.add(new JLabel()); filtros.add(btnExportar);

        add(ed, BorderLayout.CENTER);
        add(filtros, BorderLayout.SOUTH);

        // listeners
        btnGuardar.addActionListener(e -> { if (onGuardar!=null) onGuardar.run(); });
        btnEliminar.addActionListener(e -> {
            if (onEliminar!=null && !txtId.getText().isBlank())
                onEliminar.accept(Long.parseLong(txtId.getText()));
        });
        btnBuscar.addActionListener(e -> { if (onBuscar!=null) onBuscar.accept(txtBuscar.getText()); });
        btnFiltrar.addActionListener(e -> { if (onFiltrar!=null) onFiltrar.run(); });
        btnLimpiar.addActionListener(e -> { if (onLimpiar!=null) onLimpiar.run(); });
        btnExportar.addActionListener(e -> { if (onExportar!=null) onExportar.run(); });
    }

    // ==== hooks setters ====
    public void onGuardar(Runnable r){ this.onGuardar=r; }
    public void onEliminar(LongConsumer c){ this.onEliminar=c; }
    public void onBuscar(Consumer<String> c){ this.onBuscar=c; }
    public void onFiltrar(Runnable r){ this.onFiltrar=r; }
    public void onLimpiar(Runnable r){ this.onLimpiar=r; }
    public void onExportar(Runnable r){ this.onExportar=r; }

    // ==== datos ====
    public void setDependencias(List<DepOpcion> deps){
        DefaultComboBoxModel<DepOpcion> m1 = new DefaultComboBoxModel<>();
        for (DepOpcion d : deps) m1.addElement(d);
        cbDependencia.setModel(m1);

        DefaultComboBoxModel<DepOpcion> m2 = new DefaultComboBoxModel<>();
        m2.addElement(new DepOpcion(null, "(Todas)"));
        for (DepOpcion d : deps) m2.addElement(d);
        cbFiltroDep.setModel(m2);
    }

    public TipoEquipo getFiltroTipo(){ return (TipoEquipo) cbFiltroTipo.getSelectedItem(); }
    public Long getFiltroDepId(){
        DepOpcion op = (DepOpcion) cbFiltroDep.getSelectedItem();
        return op==null? null : op.getId();
    }

    public Equipo toModel(){
        var e = new Equipo();
        if (!txtId.getText().isBlank()) e.setId(Long.parseLong(txtId.getText()));
        e.setCodigo(txtCodigo.getText());
        e.setNumeroSerie(txtNumeroSerie.getText());
        e.setTipo((TipoEquipo) cbTipo.getSelectedItem());
        e.setMarca(txtMarca.getText());
        e.setModelo(txtModelo.getText());
        var dep = (DepOpcion) cbDependencia.getSelectedItem();
        e.setDependenciaId(dep==null ? null : dep.getId());
        e.setEstado((EstadoEquipo) cbEstado.getSelectedItem());
        return e;
    }
    public void fromModel(Equipo e){
        txtId.setText(e.getId()==null?"":String.valueOf(e.getId()));
        txtCodigo.setText(e.getCodigo());
        txtNumeroSerie.setText(e.getNumeroSerie());
        cbTipo.setSelectedItem(e.getTipo());
        txtMarca.setText(e.getMarca());
        txtModelo.setText(e.getModelo());
        setDependenciaSeleccionada(e.getDependenciaId());
        cbEstado.setSelectedItem(e.getEstado());
    }
    private void setDependenciaSeleccionada(Long depId){
        ComboBoxModel<DepOpcion> m = cbDependencia.getModel();
        for (int i=0;i<m.getSize();i++){
            DepOpcion op = m.getElementAt(i);
            if (op!=null && (depId!=null && depId.equals(op.getId()))){
                cbDependencia.setSelectedIndex(i); return;
            }
        }
        cbDependencia.setSelectedIndex(-1);
    }
    public void clearAndFocus(){
        txtId.setText(""); txtCodigo.setText(""); txtNumeroSerie.setText("");
        cbTipo.setSelectedIndex(0); txtMarca.setText(""); txtModelo.setText("");
        if (cbDependencia.getItemCount()>0) cbDependencia.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0); txtCodigo.requestFocus();
    }
}
