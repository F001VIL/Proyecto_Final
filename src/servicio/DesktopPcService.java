package servicio;

import dao.ConexionBD;
import dao.DesktopPcDAO;
import modelo.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DesktopPcService {
    private final DesktopPcDAO dao = new DesktopPcDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Gestión de Desktop PC ---");
            System.out.println("1. Crear Desktop PC");
            System.out.println("2. Listar Desktop PCs");
            System.out.println("3. Ver Desktop PC por ID");
            System.out.println("4. Actualizar Desktop PC");
            System.out.println("5. Eliminar Desktop PC");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createDesktopPc();
                    case "2" -> listDesktopPcs();
                    case "3" -> viewDesktopPcById();
                    case "4" -> updateDesktopPc();
                    case "5" -> deleteDesktopPc();
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

    private void createDesktopPc() throws SQLException {
        System.out.print("Procesador: ");
        String processor = scanner.nextLine().trim();
        System.out.print("RAM (GB): ");
        int ram = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Storage (GB): ");
        int storage = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Disk (GB): ");
        int disk = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\nSeleccione GraphicCard (se listan a continuación):");
        listGraphicCards();
        System.out.print("Ingrese GraphicCardID: ");
        int gcId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\nSeleccione Monitor (se listan a continuación):");
        listMonitors();
        System.out.print("Ingrese MonitorID: ");
        int monitorId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\nSeleccione Keyboard (se listan a continuación):");
        listKeyboards();
        System.out.print("Ingrese KeyboardID: ");
        int keyboardId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\nSeleccione Mouse (se listan a continuación):");
        listMice();
        System.out.print("Ingrese MouseID: ");
        int mouseId = Integer.parseInt(scanner.nextLine().trim());

        // create minimal peripheral objects and set IDs
        GraphicCard gc = new GraphicCard("", "", 0, "");
        gc.setGraphicCardID(gcId);
        Monitor monitor = new Monitor("", "", "", 0);
        monitor.setMonitorID(monitorId);
        Keyboard keyboard = new Keyboard("", "", "", "");
        keyboard.setKeyboardID(keyboardId);
        Mouse mouse = new Mouse("", "", "", false);
        mouse.setMouseID(mouseId);

        DesktopPc pc = new DesktopPc(processor, ram, storage, disk, gc, monitor, keyboard, mouse);
        // ensure code is set
        pc.setCode(pc.generateCode());

        int id = dao.insert(pc);
        System.out.println("Desktop PC creado con ID: " + id);
    }

    private void listDesktopPcs() throws SQLException {
        String sql = "SELECT ID, Processor, RAM, Storage, Disk, GraphicCardID, MonitorID, KeyboardID, MouseID, Code FROM DesktopPC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n-- Listado de Desktop PCs --");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("ID: %d | Processor: %s | RAM: %d | Storage: %d | Disk: %d | GC: %d | Monitor: %d | Keyboard: %d | Mouse: %d | Código: %s%n",
                        rs.getInt("ID"),
                        rs.getString("Processor"),
                        rs.getInt("RAM"),
                        rs.getInt("Storage"),
                        rs.getInt("Disk"),
                        rs.getInt("GraphicCardID"),
                        rs.getInt("MonitorID"),
                        rs.getInt("KeyboardID"),
                        rs.getInt("MouseID"),
                        rs.getString("Code"));
            }
            if (!any) {
                System.out.println("No hay Desktop PCs registrados.");
            }
        }
    }

    private void viewDesktopPcById() throws SQLException {
        System.out.print("Ingrese DesktopPC ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        String sql = "SELECT ID, Processor, RAM, Storage, Disk, GraphicCardID, MonitorID, KeyboardID, MouseID, Code FROM DesktopPC WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n-- Datos del Desktop PC --");
                    System.out.println("ID: " + rs.getInt("ID"));
                    System.out.println("Procesador: " + rs.getString("Processor"));
                    System.out.println("RAM: " + rs.getInt("RAM") + " GB");
                    System.out.println("Storage: " + rs.getInt("Storage") + " GB");
                    System.out.println("Disk: " + rs.getInt("Disk") + " GB");
                    System.out.println("GraphicCardID: " + rs.getInt("GraphicCardID"));
                    System.out.println("MonitorID: " + rs.getInt("MonitorID"));
                    System.out.println("KeyboardID: " + rs.getInt("KeyboardID"));
                    System.out.println("MouseID: " + rs.getInt("MouseID"));
                    System.out.println("Código: " + rs.getString("Code"));
                } else {
                    System.out.println("Desktop PC no encontrado con ID: " + id);
                }
            }
        }
    }

    private void updateDesktopPc() throws SQLException {
        System.out.print("Ingrese DesktopPC ID a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        // read existing values
        String select = "SELECT Processor, RAM, Storage, Disk, GraphicCardID, MonitorID, KeyboardID, MouseID, Code FROM DesktopPC WHERE ID = ?";
        String processor;
        int ram;
        int storage;
        int disk;
        int gcId;
        int monitorId;
        int keyboardId;
        int mouseId;
        String code;
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Desktop PC no encontrado con ID: " + id);
                    return;
                }
                processor = rs.getString("Processor");
                ram = rs.getInt("RAM");
                storage = rs.getInt("Storage");
                disk = rs.getInt("Disk");
                gcId = rs.getInt("GraphicCardID");
                monitorId = rs.getInt("MonitorID");
                keyboardId = rs.getInt("KeyboardID");
                mouseId = rs.getInt("MouseID");
                code = rs.getString("Code");
                System.out.println("Valores actuales (enter para mantener):");
                System.out.println("Procesador actual: " + processor);
                System.out.println("RAM actual: " + ram);
                System.out.println("Storage actual: " + storage);
                System.out.println("Disk actual: " + disk);
                System.out.println("GraphicCardID actual: " + gcId);
                System.out.println("MonitorID actual: " + monitorId);
                System.out.println("KeyboardID actual: " + keyboardId);
                System.out.println("MouseID actual: " + mouseId);
                System.out.println("Código actual: " + code);
            }
        }

        System.out.print("Nuevo Procesador: ");
        String newProcessor = scanner.nextLine().trim();
        System.out.print("Nueva RAM (GB): ");
        String ramInput = scanner.nextLine().trim();
        System.out.print("Nuevo Storage (GB): ");
        String storageInput = scanner.nextLine().trim();
        System.out.print("Nuevo Disk (GB): ");
        String diskInput = scanner.nextLine().trim();

        System.out.println("\nSeleccione nuevo GraphicCard (enter para mantener):");
        listGraphicCards();
        System.out.print("Ingrese GraphicCardID: ");
        String gcInput = scanner.nextLine().trim();

        System.out.println("\nSeleccione nuevo Monitor (enter para mantener):");
        listMonitors();
        System.out.print("Ingrese MonitorID: ");
        String monitorInput = scanner.nextLine().trim();

        System.out.println("\nSeleccione nuevo Keyboard (enter para mantener):");
        listKeyboards();
        System.out.print("Ingrese KeyboardID: ");
        String keyboardInput = scanner.nextLine().trim();

        System.out.println("\nSeleccione nuevo Mouse (enter para mantener):");
        listMice();
        System.out.print("Ingrese MouseID: ");
        String mouseInput = scanner.nextLine().trim();

        if (!newProcessor.isEmpty()) processor = newProcessor;
        if (!ramInput.isEmpty()) ram = Integer.parseInt(ramInput);
        if (!storageInput.isEmpty()) storage = Integer.parseInt(storageInput);
        if (!diskInput.isEmpty()) disk = Integer.parseInt(diskInput);
        if (!gcInput.isEmpty()) gcId = Integer.parseInt(gcInput);
        if (!monitorInput.isEmpty()) monitorId = Integer.parseInt(monitorInput);
        if (!keyboardInput.isEmpty()) keyboardId = Integer.parseInt(keyboardInput);
        if (!mouseInput.isEmpty()) mouseId = Integer.parseInt(mouseInput);

        GraphicCard gc = new GraphicCard("", "", 0, "");
        gc.setGraphicCardID(gcId);
        Monitor monitor = new Monitor("", "", "", 0);
        monitor.setMonitorID(monitorId);
        Keyboard keyboard = new Keyboard("", "", "", "");
        keyboard.setKeyboardID(keyboardId);
        Mouse mouse = new Mouse("", "", "", false);
        mouse.setMouseID(mouseId);

        DesktopPc pc = new DesktopPc(processor, ram, storage, disk, gc, monitor, keyboard, mouse);
        pc.setId(id);
        pc.setCode(code); // keep existing code

        boolean ok = dao.update(pc);
        System.out.println(ok ? "Desktop PC actualizado correctamente." : "No se actualizó el Desktop PC.");
    }

    private void deleteDesktopPc() throws SQLException {
        System.out.print("Ingrese DesktopPC ID a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = dao.delete(id);
        System.out.println(ok ? "Desktop PC eliminado correctamente." : "No se eliminó el Desktop PC (posible uso o no existe).");
    }

    // --- helpers to list peripherals ---

    private void listGraphicCards() throws SQLException {
        String sql = "SELECT GraphicCardID, Brand, Model, Memory, Chipset FROM GraphicCard";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            if (!any) System.out.println("No hay GraphicCards registrados.");
        }
    }

    private void listMonitors() throws SQLException {
        String sql = "SELECT MonitorID, Brand, Model, Resolution, Size FROM Monitor";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            if (!any) System.out.println("No hay Monitores registrados.");
        }
    }

    private void listKeyboards() throws SQLException {
        String sql = "SELECT KeyboardID, Brand, Model, Layout, Type FROM Keyboard";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            if (!any) System.out.println("No hay Keyboards registrados.");
        }
    }

    private void listMice() throws SQLException {
        String sql = "SELECT MouseID, Brand, Model, Type, IsWireless FROM Mouse";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            if (!any) System.out.println("No hay Mouses registrados.");
        }
    }
}
