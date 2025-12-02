package servicio;

import dao.ConexionBD;
import dao.LaptopDAO;
import modelo.Laptop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LaptopService {
    private final LaptopDAO dao = new LaptopDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Gestión de Laptops ---");
            System.out.println("1. Crear laptop");
            System.out.println("2. Listar laptops");
            System.out.println("3. Ver laptop por ID");
            System.out.println("4. Actualizar laptop");
            System.out.println("5. Eliminar laptop");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createLaptop();
                    case "2" -> listLaptops();
                    case "3" -> viewLaptopById();
                    case "4" -> updateLaptop();
                    case "5" -> deleteLaptop();
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

    private void createLaptop() throws SQLException {
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
        System.out.print("Disk (GB): ");
        int disk = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Tamaño de pantalla (pulgadas): ");
        double screenSize = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Peso (kg): ");
        double weight = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Duración de batería (horas): ");
        int batteryLife = Integer.parseInt(scanner.nextLine().trim());

        Laptop laptop = new Laptop(brand, model, processor, ram, storage, disk, screenSize, weight, batteryLife);
        laptop.setCode(laptop.generateCode());

        // try to insert, regenerate code if duplicate found (few attempts)
        int attempts = 0;
        while (attempts < 5) {
            try {
                int id = dao.insert(laptop);
                System.out.println("Laptop creada con ID: " + id);
                return;
            } catch (SQLException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                if (msg.contains("already exists") || msg.contains("ya existe")) {
                    laptop.setCode(laptop.generateCode());
                    attempts++;
                } else {
                    throw e;
                }
            }
        }
        throw new SQLException("No se pudo generar un código único para la laptop tras varios intentos.");
    }

    private void listLaptops() throws SQLException {
        String sql = "SELECT ID, Brand, Model, Processor, RAM, Storage, Disk, ScreenSize, Weight, BatteryLife, Code FROM Laptop";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Laptops --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Procesador: %s | RAM: %d GB | Storage: %d GB | Disk: %d GB | Pantalla: %.1f\" | Peso: %.2f kg | Batería: %d h | Código: %s%n",
                        rs.getInt("ID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getString("Processor"),
                        rs.getInt("RAM"),
                        rs.getInt("Storage"),
                        rs.getInt("Disk"),
                        rs.getDouble("ScreenSize"),
                        rs.getDouble("Weight"),
                        rs.getInt("BatteryLife"),
                        rs.getString("Code"));
            }
            if (!any) {
                System.out.println("No hay laptops registradas.");
            }
        }
    }

    private void viewLaptopById() throws SQLException {
        System.out.print("Ingrese Laptop ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT ID, Brand, Model, Processor, RAM, Storage, Disk, ScreenSize, Weight, BatteryLife, Code FROM Laptop WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos de la Laptop --");
                    System.out.println("ID: " + rs.getInt("ID"));
                    System.out.println("Marca: " + rs.getString("Brand"));
                    System.out.println("Modelo: " + rs.getString("Model"));
                    System.out.println("Procesador: " + rs.getString("Processor"));
                    System.out.println("RAM: " + rs.getInt("RAM") + " GB");
                    System.out.println("Storage: " + rs.getInt("Storage") + " GB");
                    System.out.println("Disk: " + rs.getInt("Disk") + " GB");
                    System.out.println("Pantalla: " + rs.getDouble("ScreenSize") + "\"");
                    System.out.println("Peso: " + rs.getDouble("Weight") + " kg");
                    System.out.println("Batería: " + rs.getInt("BatteryLife") + " h");
                    System.out.println("Código: " + rs.getString("Code"));
                } else {
                    System.out.println("Laptop no encontrada con ID: " + id);
                }
            }
        }
    }

    private void updateLaptop() throws SQLException {
        System.out.print("Ingrese Laptop ID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        String select = "SELECT Brand, Model, Processor, RAM, Storage, Disk, ScreenSize, Weight, BatteryLife, Code FROM Laptop WHERE ID = ?";
        String brand;
        String model;
        String processor;
        int ram;
        int storage;
        int disk;
        double screenSize;
        double weight;
        int batteryLife;
        String code;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Laptop no encontrada con ID: " + id);
                    return;
                }
                brand = rs.getString("Brand");
                model = rs.getString("Model");
                processor = rs.getString("Processor");
                ram = rs.getInt("RAM");
                storage = rs.getInt("Storage");
                disk = rs.getInt("Disk");
                screenSize = rs.getDouble("ScreenSize");
                weight = rs.getDouble("Weight");
                batteryLife = rs.getInt("BatteryLife");
                code = rs.getString("Code");

                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Marca actual: " + brand);
                System.out.println("Modelo actual: " + model);
                System.out.println("Procesador actual: " + processor);
                System.out.println("RAM actual: " + ram);
                System.out.println("Storage actual: " + storage);
                System.out.println("Disk actual: " + disk);
                System.out.println("Pantalla actual: " + screenSize);
                System.out.println("Peso actual: " + weight);
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
        System.out.print("Nuevo Disk (GB): ");
        String diskInput = scanner.nextLine().trim();
        System.out.print("Nuevo Tamaño de pantalla (pulgadas): ");
        String screenInput = scanner.nextLine().trim();
        System.out.print("Nuevo Peso (kg): ");
        String weightInput = scanner.nextLine().trim();
        System.out.print("Nueva Duración de batería (horas): ");
        String batteryInput = scanner.nextLine().trim();
        System.out.print("Nuevo Código (enter para mantener o dejar vacío para autogenerar): ");
        String codeInput = scanner.nextLine().trim();

        if (!newBrand.isEmpty()) brand = newBrand;
        if (!newModel.isEmpty()) model = newModel;
        if (!newProcessor.isEmpty()) processor = newProcessor;
        if (!ramInput.isEmpty()) ram = Integer.parseInt(ramInput);
        if (!storageInput.isEmpty()) storage = Integer.parseInt(storageInput);
        if (!diskInput.isEmpty()) disk = Integer.parseInt(diskInput);
        if (!screenInput.isEmpty()) screenSize = Double.parseDouble(screenInput);
        if (!weightInput.isEmpty()) weight = Double.parseDouble(weightInput);
        if (!batteryInput.isEmpty()) batteryLife = Integer.parseInt(batteryInput);
        if (!codeInput.isEmpty()) {
            code = codeInput;
        } else if (code == null || code.isEmpty()) {
            // if existing code missing, generate one
            Laptop tmp = new Laptop(brand, model, processor, ram, storage, disk, screenSize, weight, batteryLife);
            code = tmp.generateCode();
        }

        Laptop laptop = new Laptop(brand, model, processor, ram, storage, disk, screenSize, weight, batteryLife);
        laptop.setId(id);
        laptop.setCode(code);

        boolean ok = dao.update(laptop);
        System.out.println(ok ? "Laptop actualizada correctamente." : "No se actualizó la laptop.");
    }

    private void deleteLaptop() throws SQLException {
        System.out.print("Ingrese Laptop ID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Laptop eliminada correctamente." : "No se eliminó la laptop (posible uso o no existe).");
    }
}
