package util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class CsvExporter {

    public static void exportTable(JTable table, String path) throws Exception {
        TableModel m = table.getModel();

        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {

            // BOM para que Excel detecte UTF-8
            w.write('\uFEFF');

            // encabezados
            for (int c = 0; c < m.getColumnCount(); c++) {
                w.write(m.getColumnName(c));
                if (c < m.getColumnCount() - 1) w.write(";");
            }
            w.write("\n");

            // filas
            for (int r = 0; r < m.getRowCount(); r++) {
                for (int c = 0; c < m.getColumnCount(); c++) {
                    Object v = m.getValueAt(r, c);
                    String s = v == null ? "" : v.toString().replace(";", ",");
                    w.write(s);
                    if (c < m.getColumnCount() - 1) w.write(";");
                }
                w.write("\n");
            }
        }
    }
}
