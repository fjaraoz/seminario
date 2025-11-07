package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class AuditConfig {

    private static final Path FILE = Path.of("config", "audit-mode.txt");

    public static void saveMode(boolean modoDetallado) {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = new OutputStreamWriter(
                    new FileOutputStream(FILE.toFile()), StandardCharsets.UTF_8)) {
                w.write(modoDetallado ? "DETALLADO" : "BASICO");
            }
        } catch (IOException e) {
            // para el TP alcanza con loguear en consola
            e.printStackTrace();
        }
    }

    public static boolean loadMode() {
        try {
            if (!Files.exists(FILE)) return false; // por defecto BASICO
            String txt = Files.readString(FILE, StandardCharsets.UTF_8).trim();
            return "DETALLADO".equalsIgnoreCase(txt);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
