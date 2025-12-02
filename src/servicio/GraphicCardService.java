package servicio;

import dao.ConexionBD;
import dao.GraphicCardDAO;
import modelo.GraphicCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GraphicCardService {
    private final GraphicCardDAO dao = new GraphicCardDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Gestión de Tarjetas Gráficas ---");
            System.out.println("1. Crear tarjeta gráfica");
            System.out.println("2. Listar tarjetas gráficas");
            System.out.println("3. Ver tarjeta gráfica por ID");
            System.out.println("4. Actualizar tarjeta gráfica");
            System.out.println("5. Eliminar tarjeta gráfica");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createGraphicCard();
                    case "2" -> listGraphicCards();
                    case "3" -> viewGraphicCardById();
                    case "4" -> updateGraphicCard();
                    case "5" -> deleteGraphicCard();
                    case "6" -> {
                        System.out.println("Saliendo...");
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

    private void createGraphicCard() throws SQLException {
        System.out.print("Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Memoria (MB): ");
        int memory = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Chipset: ");
        String chipset = scanner.nextLine().trim();

        GraphicCard gc = new GraphicCard(brand, model, memory, chipset);
        int id = dao.insert(gc);
        System.out.println("Tarjeta gráfica creada con ID: " + id);
    }

    private void listGraphicCards() throws SQLException {
        String sql = "SELECT GraphicCardID, Brand, Model, Memory, Chipset FROM GraphicCard";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Tarjetas Gráficas --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Memoria: %d MB | Chipset: %s%n",
                        rs.getInt("GraphicCardID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getInt("Memory"),
                        rs.getString("Chipset"));
            }
            if (!any) {
                System.out.println("No hay tarjetas gráficas registradas.");
            }
        }
    }

    private void viewGraphicCardById() throws SQLException {
        System.out.print("Ingrese GraphicCardID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT GraphicCardID, Brand, Model, Memory, Chipset FROM GraphicCard WHERE GraphicCardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos de la Tarjeta Gráfica --");
                    System.out.println("ID: " + rs.getInt("GraphicCardID"));
                    System.out.println("Marca: " + rs.getString("Brand"));
                    System.out.println("Modelo: " + rs.getString("Model"));
                    System.out.println("Memoria: " + rs.getInt("Memory") + " MB");
                    System.out.println("Chipset: " + rs.getString("Chipset"));
                } else {
                    System.out.println("Tarjeta gráfica no encontrada con ID: " + id);
                }
            }
        }
    }

    private void updateGraphicCard() throws SQLException {
        System.out.print("Ingrese GraphicCardID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        String select = "SELECT Brand, Model, Memory, Chipset FROM GraphicCard WHERE GraphicCardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Tarjeta gráfica no encontrada con ID: " + id);
                    return;
                }
                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Marca actual: " + rs.getString("Brand"));
                System.out.println("Modelo actual: " + rs.getString("Model"));
                System.out.println("Memoria actual: " + rs.getInt("Memory") + " MB");
                System.out.println("Chipset actual: " + rs.getString("Chipset"));
            }
        }

        System.out.print("Nueva Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Nuevo Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Nueva Memoria (MB): ");
        String memoryInput = scanner.nextLine().trim();
        System.out.print("Nuevo Chipset: ");
        String chipset = scanner.nextLine().trim();

        if (brand.isEmpty() || model.isEmpty() || memoryInput.isEmpty() || chipset.isEmpty()) {
            String sql = "SELECT Brand, Model, Memory, Chipset FROM GraphicCard WHERE GraphicCardID = ?";
            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (brand.isEmpty()) brand = rs.getString("Brand");
                        if (model.isEmpty()) model = rs.getString("Model");
                        if (memoryInput.isEmpty()) memoryInput = String.valueOf(rs.getInt("Memory"));
                        if (chipset.isEmpty()) chipset = rs.getString("Chipset");
                    } else {
                        System.out.println("No se pudo leer la tarjeta gráfica existente.");
                        return;
                    }
                }
            }
        }

        int memory = Integer.parseInt(memoryInput);
        GraphicCard gc = new GraphicCard(brand, model, memory, chipset);
        gc.setGraphicCardID(id);
        boolean ok = dao.update(gc);
        System.out.println(ok ? "Tarjeta gráfica actualizada correctamente." : "No se actualizó la tarjeta gráfica.");
    }

    private void deleteGraphicCard() throws SQLException {
        System.out.print("Ingrese GraphicCardID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Tarjeta gráfica eliminada correctamente." : "No se eliminó la tarjeta gráfica (posible uso o no existe).");
    }
}
