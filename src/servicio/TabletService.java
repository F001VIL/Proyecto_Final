package servicio;

import dao.ConexionBD;
import dao.TabletDAO;
import modelo.Tablet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TabletService {
    private final TabletDAO dao = new TabletDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Gestión de Tablets ---");
            System.out.println("1. Crear tablet");
            System.out.println("2. Listar tablets");
            System.out.println("3. Ver tablet por ID");
            System.out.println("4. Actualizar tablet");
            System.out.println("5. Eliminar tablet");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createTablet();
                    case "2" -> listTablets();
                    case "3" -> viewTabletById();
                    case "4" -> updateTablet();
                    case "5" -> deleteTablet();
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

    private void createTablet() throws SQLException {
        System.out.print("Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Procesador: ");
        String processor = scanner.nextLine().trim();
        System.out.print("RAM (GB): ");
        int ram = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Storage (GB): ");
        int storage = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Duración de batería (horas): ");
        int batteryLife = Integer.parseInt(scanner.nextLine().trim());

        Tablet tablet = new Tablet(brand, model, processor, ram, storage, batteryLife);
        tablet.setCode(tablet.generateCode());

        int attempts = 0;
        while (attempts < 5) {
            try {
                int id = dao.insert(tablet);
                System.out.println("Tablet creada con ID: " + id);
                return;
            } catch (SQLException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                if (msg.contains("already exists") || msg.contains("ya existe") || msg.contains("duplicate")) {
                    tablet.setCode(tablet.generateCode());
                    attempts++;
                } else {
                    throw e;
                }
            }
        }
        throw new SQLException("No se pudo generar un código único para la tablet tras varios intentos.");
    }

    private void listTablets() throws SQLException {
        String sql = "SELECT ID, Brand, Model, Processor, RAM, Storage, BatteryLife, Code FROM Tablet";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Tablets --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Procesador: %s | RAM: %d GB | Storage: %d GB | Batería: %d h | Código: %s%n",
                        rs.getInt("ID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getString("Processor"),
                        rs.getInt("RAM"),
                        rs.getInt("Storage"),
                        rs.getInt("BatteryLife"),
                        rs.getString("Code"));
            }
            if (!any) {
                System.out.println("No hay tablets registradas.");
            }
        }
    }

    private void viewTabletById() throws SQLException {
        System.out.print("Ingrese Tablet ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT ID, Brand, Model, Processor, RAM, Storage, BatteryLife, Code FROM Tablet WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos de la Tablet --");
                    System.out.println("ID: " + rs.getInt("ID"));
                    System.out.println("Marca: " + rs.getString("Brand"));
                    System.out.println("Modelo: " + rs.getString("Model"));
                    System.out.println("Procesador: " + rs.getString("Processor"));
                    System.out.println("RAM: " + rs.getInt("RAM") + " GB");
                    System.out.println("Storage: " + rs.getInt("Storage") + " GB");
                    System.out.println("Batería: " + rs.getInt("BatteryLife") + " h");
                    System.out.println("Código: " + rs.getString("Code"));
                } else {
                    System.out.println("Tablet no encontrada con ID: " + id);
                }
            }
        }
    }

    private void updateTablet() throws SQLException {
        System.out.print("Ingrese Tablet ID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        String select = "SELECT Brand, Model, Processor, RAM, Storage, BatteryLife, Code FROM Tablet WHERE ID = ?";
        String brand;
        String model;
        String processor;
        int ram;
        int storage;
        int batteryLife;
        String code;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Tablet no encontrada con ID: " + id);
                    return;
                }
                brand = rs.getString("Brand");
                model = rs.getString("Model");
                processor = rs.getString("Processor");
                ram = rs.getInt("RAM");
                storage = rs.getInt("Storage");
                batteryLife = rs.getInt("BatteryLife");
                code = rs.getString("Code");

                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Marca actual: " + brand);
                System.out.println("Modelo actual: " + model);
                System.out.println("Procesador actual: " + processor);
                System.out.println("RAM actual: " + ram);
                System.out.println("Storage actual: " + storage);
                System.out.println("Batería actual: " + batteryLife);
                System.out.println("Código actual: " + code);
            }
        }

        System.out.print("Nueva Marca: ");
        String newBrand = scanner.nextLine().trim();
        System.out.print("Nuevo Modelo: ");
        String newModel = scanner.nextLine().trim();
        System.out.print("Nuevo Procesador: ");
        String newProcessor = scanner.nextLine().trim();
        System.out.print("Nueva RAM (GB): ");
        String ramInput = scanner.nextLine().trim();
        System.out.print("Nuevo Storage (GB): ");
        String storageInput = scanner.nextLine().trim();
        System.out.print("Nueva Duración de batería (horas): ");
        String batteryInput = scanner.nextLine().trim();
        System.out.print("Nuevo Código (enter para mantener o dejar vacío para autogenerar): ");
        String codeInput = scanner.nextLine().trim();

        if (!newBrand.isEmpty()) brand = newBrand;
        if (!newModel.isEmpty()) model = newModel;
        if (!newProcessor.isEmpty()) processor = newProcessor;
        if (!ramInput.isEmpty()) ram = Integer.parseInt(ramInput);
        if (!storageInput.isEmpty()) storage = Integer.parseInt(storageInput);
        if (!batteryInput.isEmpty()) batteryLife = Integer.parseInt(batteryInput);

        if (!codeInput.isEmpty()) {
            code = codeInput;
        } else if (code == null || code.isEmpty()) {
            Tablet tmp = new Tablet(brand, model, processor, ram, storage, batteryLife);
            code = tmp.generateCode();
        }

        Tablet tablet = new Tablet(brand, model, processor, ram, storage, batteryLife);
        tablet.setId(id);
        tablet.setCode(code);

        boolean ok = dao.update(tablet);
        System.out.println(ok ? "Tablet actualizada correctamente." : "No se actualizó la tablet.");
    }

    private void deleteTablet() throws SQLException {
        System.out.print("Ingrese Tablet ID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Tablet eliminada correctamente." : "No se eliminó la tablet (posible uso o no existe).");
    }
}
