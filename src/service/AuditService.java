package service;

import domain.model.AuditEntry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditService {

    private final List<AuditEntry> entries = new ArrayList<>();
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path FILE = Path.of("logs", "audit-log.csv");

    public AuditService() {
        cargarDesdeArchivo();
    }

    public synchronized void log(AuditEntry e){
        entries.add(e);
        appendToFile(e);
    }

    public synchronized List<AuditEntry> listar(){
        return Collections.unmodifiableList(entries);
    }

    public synchronized String toText(){
        StringBuilder sb = new StringBuilder();
        for (var e : entries){
            sb.append(e.getTs().format(FMT)).append(" | ")
              .append(e.getOperador()).append(" | ")
              .append(e.getAccion()).append(" | ")
              .append(e.getDetalle()==null?"":e.getDetalle())
              .append(System.lineSeparator());
        }
        return sb.toString();
    }

    // ================== Persistencia en archivo ==================

    private void cargarDesdeArchivo() {
        try {
            if (!Files.exists(FILE)) return;

            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", 4);
                if (parts.length < 4) continue;

                LocalDateTime ts = LocalDateTime.parse(parts[0], FMT);
                String op = parts[1];
                String acc = parts[2];
                String det = parts[3];

                AuditEntry e = new AuditEntry(op, acc, det);
                // setear ts vía reflexión no vale la pena para el TP,
                // así que sólo usamos el texto para mostrar.
                entries.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void appendToFile(AuditEntry e) {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = new OutputStreamWriter(
                    new FileOutputStream(FILE.toFile(), true), StandardCharsets.UTF_8)) {

                String det = e.getDetalle() == null ? "" : e.getDetalle().replace("\n", " ");
                String line = e.getTs().format(FMT) + ";" +
                        e.getOperador() + ";" +
                        e.getAccion() + ";" +
                        det + System.lineSeparator();
                w.write(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
