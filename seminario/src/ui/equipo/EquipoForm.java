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
    private final JTextField txtBuscar=new JTextField(18);
    private final JButton btnBuscar=new JButton("Buscar");

    private Runnable onGuardar; private LongConsumer onEliminar; private Consumer<String> onBuscar;

    public EquipoForm(){
        setLayout(new GridLayout(0,2,6,6));
        txtId.setEditable(false);

        add(new JLabel("ID:")); add(txtId);
        add(new JLabel("Código inventario:")); add(txtCodigo);
        add(new JLabel("Nº de serie:")); add(txtNumeroSerie);
        add(new JLabel("Tipo:")); add(cbTipo);
        add(new JLabel("Marca:")); add(txtMarca);
        add(new JLabel("Modelo:")); add(txtModelo);
        add(new JLabel("Dependencia:")); add(cbDependencia);
        add(new JLabel("Estado:")); add(cbEstado);

        add(btnGuardar); add(btnEliminar);
        add(new JLabel("Buscar texto:")); add(txtBuscar);
        add(new JLabel("")); add(btnBuscar);

        btnGuardar.addActionListener(e -> { if (onGuardar!=null) onGuardar.run(); });
        btnEliminar.addActionListener(e -> {
            if (onEliminar!=null && !txtId.getText().isBlank())
                onEliminar.accept(Long.parseLong(txtId.getText()));
        });
        btnBuscar.addActionListener(e -> { if (onBuscar!=null) onBuscar.accept(txtBuscar.getText()); });
    }

    /* ==== hooks ==== */
    public void onGuardar(Runnable r){ this.onGuardar=r; }
    public void onEliminar(LongConsumer c){ this.onEliminar=c; }
    public void onBuscar(Consumer<String> c){ this.onBuscar=c; }

    /* ==== datos ==== */
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

    public void clearAndFocus(){
        txtId.setText("");
        txtCodigo.setText("");
        txtNumeroSerie.setText("");
        cbTipo.setSelectedIndex(0);
        txtMarca.setText("");
        txtModelo.setText("");
        if (cbDependencia.getItemCount()>0) cbDependencia.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        txtCodigo.requestFocus();
    }

    /** Recibe las dependencias y llena la combo (id + nombre) */
    public void setDependencias(List<DepOpcion> deps){
        DefaultComboBoxModel<DepOpcion> model = new DefaultComboBoxModel<>();
        for (DepOpcion d : deps) model.addElement(d);
        cbDependencia.setModel(model);
    }

    /** Selecciona la dependencia por id (si existe en la combo) */
    private void setDependenciaSeleccionada(Long dependenciaId){
        if (dependenciaId == null) { cbDependencia.setSelectedIndex(-1); return; }
        ComboBoxModel<DepOpcion> m = cbDependencia.getModel();
        for (int i=0;i<m.getSize();i++){
            DepOpcion op = m.getElementAt(i);
            if (op!=null && dependenciaId.equals(op.getId())) {
                cbDependencia.setSelectedIndex(i);
                return;
            }
        }
        cbDependencia.setSelectedIndex(-1);
    }

    /* ==== mensajes ==== */
    public void showInfo(String m){ JOptionPane.showMessageDialog(this,m,"Info",JOptionPane.INFORMATION_MESSAGE); }
    public void showError(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
    public void showWarn(String m){ JOptionPane.showMessageDialog(this,m,"Aviso",JOptionPane.WARNING_MESSAGE); }
}
