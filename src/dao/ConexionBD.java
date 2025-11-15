package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = 
        "jdbc:sqlserver://localhost:1434;databaseName=BIBLIOTECA_UNIVERSITARIA;encrypt=false";
    private static final String USER = "admin";  
    private static final String PASSWORD = "Admin2025*"; 

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }

    // Método de prueba (para ejecutar desde terminal)
    public static void main(String[] args) {
        getConnection();
    }
}

