package servicio;

import dao.ConexionBD;
import dao.KeyboardDAO;
import modelo.Keyboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class KeyboardManager {
    private final KeyboardDAO dao = new KeyboardDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Gestión de Teclados ---");
            System.out.println("1. Crear teclado");
            System.out.println("2. Listar teclados");
            System.out.println("3. Ver teclado por ID");
            System.out.println("4. Actualizar teclado");
            System.out.println("5. Eliminar teclado");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createKeyboard();
                    case "2" -> listKeyboards();
                    case "3" -> viewKeyboardById();
                    case "4" -> updateKeyboard();
                    case "5" -> deleteKeyboard();
                    case "6" -> {
                        System.out.println("Saliendo...");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Entrada numérica no válida.");
            }
        }
    }

    private void createKeyboard() throws SQLException {
        System.out.print("Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Layout: ");
        String layout = scanner.nextLine().trim();
        System.out.print("Tipo (por ejemplo, mechanical, membrane): ");
        String type = scanner.nextLine().trim();

        Keyboard keyboard = new Keyboard(brand, model, layout, type);
        int id = dao.insert(keyboard);
        System.out.println("Teclado creado con ID: " + id);
    }

    private void listKeyboards() throws SQLException {
        String sql = "SELECT KeyboardID, Brand, Model, Layout, Type FROM Keyboard";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Teclados --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Layout: %s | Tipo: %s%n",
                        rs.getInt("KeyboardID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getString("Layout"),
                        rs.getString("Type"));
            }
            if (!any) {
                System.out.println("No hay teclados registrados.");
            }
        }
    }

    private void viewKeyboardById() throws SQLException {
        System.out.print("Ingrese KeyboardID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT KeyboardID, Brand, Model, Layout, Type FROM Keyboard WHERE KeyboardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos del Teclado --");
                    System.out.println("ID: " + rs.getInt("KeyboardID"));
                    System.out.println("Marca: " + rs.getString("Brand"));
                    System.out.println("Modelo: " + rs.getString("Model"));
                    System.out.println("Layout: " + rs.getString("Layout"));
                    System.out.println("Tipo: " + rs.getString("Type"));
                } else {
                    System.out.println("Teclado no encontrado con ID: " + id);
                }
            }
        }
    }

    private void updateKeyboard() throws SQLException {
        System.out.print("Ingrese KeyboardID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        String select = "SELECT Brand, Model, Layout, Type FROM Keyboard WHERE KeyboardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Teclado no encontrado con ID: " + id);
                    return;
                }
                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Marca actual: " + rs.getString("Brand"));
                System.out.println("Modelo actual: " + rs.getString("Model"));
                System.out.println("Layout actual: " + rs.getString("Layout"));
                System.out.println("Tipo actual: " + rs.getString("Type"));
            }
        }

        System.out.print("Nueva Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Nuevo Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Nuevo Layout: ");
        String layout = scanner.nextLine().trim();
        System.out.print("Nuevo Tipo: ");
        String type = scanner.nextLine().trim();

        if (brand.isEmpty() || model.isEmpty() || layout.isEmpty() || type.isEmpty()) {
            String sql = "SELECT Brand, Model, Layout, Type FROM Keyboard WHERE KeyboardID = ?";
            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (brand.isEmpty()) brand = rs.getString("Brand");
                        if (model.isEmpty()) model = rs.getString("Model");
                        if (layout.isEmpty()) layout = rs.getString("Layout");
                        if (type.isEmpty()) type = rs.getString("Type");
                    } else {
                        System.out.println("No se pudo leer el teclado existente.");
                        return;
                    }
                }
            }
        }

        Keyboard keyboard = new Keyboard(brand, model, layout, type);
        keyboard.setKeyboardID(id);
        boolean ok = dao.update(keyboard);
        System.out.println(ok ? "Teclado actualizado correctamente." : "No se actualizó el teclado.");
    }

    private void deleteKeyboard() throws SQLException {
        System.out.print("Ingrese KeyboardID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Teclado eliminado correctamente." : "No se eliminó el teclado (posible uso o no existe).");
    }
}
