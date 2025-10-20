package app;

import domain.model.Role;

public class Session {
    private static String operador;
    private static Role role;

    public static void start(String op, Role r){ operador = op; role = r; }
    public static String operador(){ return operador; }
    public static Role role(){ return role; }
}
