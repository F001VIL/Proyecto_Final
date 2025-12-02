package servicio;

import dao.ConexionBD;
import dao.MouseDAO;
import modelo.Mouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MouseManager {
    private final MouseDAO dao = new MouseDAO();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new MouseManager().run();
    }

    private void run() {
        while (true) {
            System.out.println("\n--- Gestión de Mouses ---");
            System.out.println("1. Crear mouse");
            System.out.println("2. Listar mouses");
            System.out.println("3. Ver mouse por ID");
            System.out.println("4. Actualizar mouse");
            System.out.println("5. Eliminar mouse");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createMouse();
                    case "2" -> listMice();
                    case "3" -> viewMouseById();
                    case "4" -> updateMouse();
                    case "5" -> deleteMouse();
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

    private void createMouse() throws SQLException {
        System.out.print("Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Tipo: ");
        String type = scanner.nextLine().trim();
        System.out.print("¿Es inalámbrico? (s/n): ");
        boolean isWireless = parseYesNo(scanner.nextLine().trim());

        Mouse mouse = new Mouse(brand, model, type, isWireless);
        int id = dao.insert(mouse);
        System.out.println("Mouse creado con ID: " + id);
    }

    private void listMice() throws SQLException {
        String sql = "SELECT MouseID, Brand, Model, Type, IsWireless FROM Mouse";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Mouses --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Tipo: %s | Inalámbrico: %s%n",
                        rs.getInt("MouseID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getString("Type"),
                        rs.getBoolean("IsWireless") ? "Sí" : "No");
            }
            if (!any) {
                System.out.println("No hay mouses registrados.");
            }
        }
    }

    private void viewMouseById() throws SQLException {
        System.out.print("Ingrese MouseID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT MouseID, Brand, Model, Type, IsWireless FROM Mouse WHERE MouseID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos del Mouse --");
                    System.out.println("ID: " + rs.getInt("MouseID"));
                    System.out.println("Marca: " + rs.getString("Brand"));
                    System.out.println("Modelo: " + rs.getString("Model"));
                    System.out.println("Tipo: " + rs.getString("Type"));
                    System.out.println("Inalámbrico: " + (rs.getBoolean("IsWireless") ? "Sí" : "No"));
                } else {
                    System.out.println("Mouse no encontrado con ID: " + id);
                }
            }
        }
    }

    private void updateMouse() throws SQLException {
        System.out.print("Ingrese MouseID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        String select = "SELECT Brand, Model, Type, IsWireless FROM Mouse WHERE MouseID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Mouse no encontrado con ID: " + id);
                    return;
                }
                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Marca actual: " + rs.getString("Brand"));
                System.out.println("Modelo actual: " + rs.getString("Model"));
                System.out.println("Tipo actual: " + rs.getString("Type"));
                System.out.println("Inalámbrico actual: " + (rs.getBoolean("IsWireless") ? "Sí" : "No"));
            }
        }

        System.out.print("Nueva Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Nuevo Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Nuevo Tipo: ");
        String type = scanner.nextLine().trim();
        System.out.print("¿Es inalámbrico? (s/n, enter para mantener): ");
        String wirelessInput = scanner.nextLine().trim();

        if (brand.isEmpty() || model.isEmpty() || type.isEmpty() || wirelessInput.isEmpty()) {
            String sql = "SELECT Brand, Model, Type, IsWireless FROM Mouse WHERE MouseID = ?";
            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (brand.isEmpty()) brand = rs.getString("Brand");
                        if (model.isEmpty()) model = rs.getString("Model");
                        if (type.isEmpty()) type = rs.getString("Type");
                        if (wirelessInput.isEmpty()) wirelessInput = rs.getBoolean("IsWireless") ? "s" : "n";
                    } else {
                        System.out.println("No se pudo leer el mouse existente.");
                        return;
                    }
                }
            }
        }

        boolean isWireless = parseYesNo(wirelessInput);
        Mouse mouse = new Mouse(brand, model, type, isWireless);
        mouse.setMouseID(id);
        boolean ok = dao.update(mouse);
        System.out.println(ok ? "Mouse actualizado correctamente." : "No se actualizó el mouse.");
    }

    private void deleteMouse() throws SQLException {
        System.out.print("Ingrese MouseID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Mouse eliminado correctamente." : "No se eliminó el mouse (posible uso o no existe).");
    }

    private boolean parseYesNo(String input) {
        if (input == null) return false;
        input = input.trim().toLowerCase();
        return input.equals("s") || input.equals("y") || input.equals("yes") || input.equals("si");
    }
}
