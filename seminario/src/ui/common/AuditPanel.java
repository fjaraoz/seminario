package ui.common;

import service.AuditService;

import javax.swing.*;
import java.awt.*;

public class AuditPanel extends JPanel {
    private final JTextArea area = new JTextArea(12, 60);
    private final JButton btnRefrescar = new JButton("Refrescar");

    public AuditPanel(AuditService auditService){
        setLayout(new BorderLayout(6,6));
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(btnRefrescar, BorderLayout.SOUTH);
        btnRefrescar.addActionListener(e -> area.setText(auditService.toText()));
    }
}
