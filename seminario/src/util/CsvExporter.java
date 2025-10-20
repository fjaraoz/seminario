package util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;

public class CsvExporter {
    public static void exportTable(JTable table, String path) throws Exception {
        TableModel m = table.getModel();
        try (FileWriter w = new FileWriter(path)) {
            // encabezados
            for (int c=0;c<m.getColumnCount();c++){
                w.write(m.getColumnName(c));
                if (c<m.getColumnCount()-1) w.write(";");
            }
            w.write("\n");
            // filas
            for (int r=0;r<m.getRowCount();r++){
                for (int c=0;c<m.getColumnCount();c++){
                    Object v = m.getValueAt(r,c);
                    String s = v==null? "" : v.toString().replace(";", ",");
                    w.write(s);
                    if (c<m.getColumnCount()-1) w.write(";");
                }
                w.write("\n");
            }
        }
    }
}
