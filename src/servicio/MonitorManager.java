package servicio;

import dao.ConexionBD;
import dao.MonitorDAO;
import modelo.Monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MonitorManager {
    private final MonitorDAO dao = new MonitorDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Gestión de Monitores ---");
            System.out.println("1. Crear monitor");
            System.out.println("2. Listar monitores");
            System.out.println("3. Ver monitor por ID");
            System.out.println("4. Actualizar monitor");
            System.out.println("5. Eliminar monitor");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> crearMonitor();
                    case "2" -> listarMonitores();
                    case "3" -> verMonitorPorId();
                    case "4" -> actualizarMonitor();
                    case "5" -> eliminarMonitor();
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

    private void crearMonitor() throws SQLException {
        System.out.print("Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Resolución: ");
        String resolution = scanner.nextLine().trim();
        System.out.print("Tamaño (pulgadas): ");
        int size = Integer.parseInt(scanner.nextLine().trim());

        Monitor m = new Monitor(brand, model, resolution, size);
        int id = dao.insert(m);
        System.out.println("Monitor creado con ID: " + id);
    }

    private void listarMonitores() throws SQLException {
        String sql = "SELECT MonitorID, Brand, Model, Resolution, Size FROM Monitor";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Monitores --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Resolución: %s | Tamaño: %d\"%n",
                        rs.getInt("MonitorID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getString("Resolution"),
                        rs.getInt("Size"));
            }
            if (!any) {
                System.out.println("No hay monitores registrados.");
            }
        }
    }

    private void verMonitorPorId() throws SQLException {
        System.out.print("Ingrese MonitorID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT MonitorID, Brand, Model, Resolution, Size FROM Monitor WHERE MonitorID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos del Monitor --");
                    System.out.println("ID: " + rs.getInt("MonitorID"));
                    System.out.println("Marca: " + rs.getString("Brand"));
                    System.out.println("Modelo: " + rs.getString("Model"));
                    System.out.println("Resolución: " + rs.getString("Resolution"));
                    System.out.println("Tamaño: " + rs.getInt("Size") + "\"");
                } else {
                    System.out.println("Monitor no encontrado con ID: " + id);
                }
            }
        }
    }

    private void actualizarMonitor() throws SQLException {
        System.out.print("Ingrese MonitorID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        // Mostrar actual (si existe)
        String select = "SELECT Brand, Model, Resolution, Size FROM Monitor WHERE MonitorID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Monitor no encontrado con ID: " + id);
                    return;
                }
                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Marca actual: " + rs.getString("Brand"));
                System.out.println("Modelo actual: " + rs.getString("Model"));
                System.out.println("Resolución actual: " + rs.getString("Resolution"));
                System.out.println("Tamaño actual: " + rs.getInt("Size"));
            }
        }

        System.out.print("Nueva Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Nuevo Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Nueva Resolución: ");
        String resolution = scanner.nextLine().trim();
        System.out.print("Nuevo Tamaño (pulgadas): ");
        String sizeInput = scanner.nextLine().trim();

        // If user left input empty, fetch existing values to keep them
        if (brand.isEmpty() || model.isEmpty() || resolution.isEmpty() || sizeInput.isEmpty()) {
            String sql = "SELECT Brand, Model, Resolution, Size FROM Monitor WHERE MonitorID = ?";
            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (brand.isEmpty()) brand = rs.getString("Brand");
                        if (model.isEmpty()) model = rs.getString("Model");
                        if (resolution.isEmpty()) resolution = rs.getString("Resolution");
                        if (sizeInput.isEmpty()) sizeInput = String.valueOf(rs.getInt("Size"));
                    } else {
                        System.out.println("No se pudo leer el monitor existente.");
                        return;
                    }
                }
            }
        }

        int size = Integer.parseInt(sizeInput);
        Monitor m = new Monitor(brand, model, resolution, size);
        m.setMonitorID(id);
        boolean ok = dao.update(m);
        System.out.println(ok ? "Monitor actualizado correctamente." : "No se actualizó el monitor.");
    }

    private void eliminarMonitor() throws SQLException {
        System.out.print("Ingrese MonitorID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Monitor eliminado correctamente." : "No se eliminó el monitor (posible uso o no existe).");
    }
}
