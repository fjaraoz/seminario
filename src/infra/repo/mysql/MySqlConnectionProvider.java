package infra.repo.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnectionProvider {

    private static final String URL =
    "jdbc:mysql://localhost:3306/inventario_it"
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=America/Argentina/Buenos_Aires"
        + "&useUnicode=true"
        + "&characterEncoding=UTF-8";

    private static final String USER = "root";        // o el usuario que uses en Workbench
    private static final String PASSWORD = "Nameless_77";  // contraseña real

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver de MySQL.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

