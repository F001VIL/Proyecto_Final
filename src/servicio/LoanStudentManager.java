package servicio;

import dao.ConexionBD;
import dao.LoanDAO;
import modelo.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

public class LoanStudentManager {
    private final LoanDAO loanDAO = new LoanDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void run(int userId) {
        while (true) {
            System.out.println();
            System.out.println("===== Menú de préstamos (Usuario ID: " + userId + ") =====");
            System.out.println("1) Solicitar préstamo");
            System.out.println("2) Listar mis préstamos");
            System.out.println("0) Salir");
            System.out.print("Seleccione opción: ");

            String opt = scanner.nextLine().trim();
            if (opt.isEmpty()) continue;

            switch (opt) {
                case "1":
                    requestLoan(userId);
                    break;
                case "2":
                    listLoans(userId);
                    break;
                case "0":
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void requestLoan(int userId) {
        try {
            if (loanDAO.userHasActiveLoan(userId)) {
                System.out.println("No puede solicitar: ya tiene un préstamo activo o pendiente.");
                return;
            }
        } catch (RuntimeException e) {
            System.out.println("Error comprobando estado de préstamos: " + e.getMessage());
            return;
        }

        System.out.println("\n-- Solicitar préstamo --");
        System.out.println("Seleccione tipo de recurso:");
        System.out.println("1) Desktop PC");
        System.out.println("2) Laptop");
        System.out.println("3) Tablet");
        System.out.print("Opción: ");
        String t = scanner.nextLine().trim();

        Loan.ElementType et;
        switch (t) {
            case "1": et = Loan.ElementType.DESKTOP_PC; break;
            case "2": et = Loan.ElementType.LAPTOP; break;
            case "3": et = Loan.ElementType.TABLET; break;
            default:
                System.out.println("Tipo no válido.");
                return;
        }

        // Use LoanDAO to list available resources for the selected type
        java.util.List<dao.LoanDAO.AvailableResource> available;
        try {
            available = loanDAO.getAvailableResourcesByType(et);
        } catch (RuntimeException ex) {
            System.out.println("Error al obtener recursos disponibles: " + ex.getMessage());
            return;
        }

        if (available == null || available.isEmpty()) {
            System.out.println("No hay recursos disponibles de ese tipo en este momento.");
            return;
        }

        System.out.println("\nRecursos disponibles:");
        for (dao.LoanDAO.AvailableResource ar : available) {
            String label = ar.getLabel() != null && !ar.getLabel().isEmpty() ? ar.getLabel() : "(sin nombre)";
            System.out.println(" - ID: " + ar.getElementId() + "  Nombre: " + label);
        }
        System.out.println();

        System.out.print("Ingrese el ID del recurso que desea solicitar (tal como aparece en la lista): ");
        String eidStr = scanner.nextLine().trim();
        int elementId;
        try {
            elementId = Integer.parseInt(eidStr);
            if (elementId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            System.out.println("ID inválido.");
            return;
        }

        // Validate the chosen elementId is in the available list
        boolean found = false;
        for (dao.LoanDAO.AvailableResource ar : available) {
            if (ar.getElementId() == elementId) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("El ID seleccionado no está en la lista de recursos disponibles.");
            return;
        }

        System.out.print("Notas (opcional): ");
        String notes = scanner.nextLine().trim();
        Loan loan = new Loan(null, userId, et, elementId, Loan.Status.PENDING,
                LocalDateTime.now(), null, null, notes.isEmpty() ? null : notes);

        try {
            int generatedId = loanDAO.requestLoan(loan);
            System.out.println("Solicitud registrada con ID: " + generatedId + ". Estado: PENDING.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Solicitud inválida: " + iae.getMessage());
        } catch (IllegalStateException ise) {
            System.out.println("No se pudo solicitar: " + ise.getMessage());
        } catch (RuntimeException re) {
            System.out.println("Error al registrar la solicitud: " + re.getMessage());
        }
    }

    private void listLoans(int userId) {
        System.out.println("\n-- Mis préstamos --");
        String sql = "SELECT LoanID, ElementType, ElementID, Status, LoanDate, DueDate, ReturnDate, Notes " +
                "FROM Loan WHERE UserID = ? ORDER BY LoanDate DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Error: conexión nula.");
                return;
            }

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    int id = rs.getInt("LoanID");
                    String etStr = rs.getString("ElementType");
                    int elementId = rs.getInt("ElementID");
                    String statusStr = rs.getString("Status");
                    Timestamp loanTs = rs.getTimestamp("LoanDate");
                    Timestamp dueTs = rs.getTimestamp("DueDate");
                    Timestamp retTs = rs.getTimestamp("ReturnDate");
                    String notes = rs.getString("Notes");

                    Loan.ElementType et = dbToElementType(etStr);
                    Loan.Status status = dbToStatus(statusStr);

                    System.out.println("----------------------------------------");
                    System.out.println("ID: " + id);
                    System.out.println("Recurso: " + (et != null ? et.name() : etStr) + " (ID " + elementId + ")");
                    System.out.println("Estado: " + (status != null ? status.name() : statusStr));
                    System.out.println("Fecha préstamo: " + (loanTs != null ? loanTs.toLocalDateTime() : "N/A"));
                    System.out.println("Fecha vencimiento: " + (dueTs != null ? dueTs.toLocalDateTime() : "N/A"));
                    System.out.println("Fecha devolución: " + (retTs != null ? retTs.toLocalDateTime() : "N/A"));
                    System.out.println("Notas: " + (notes != null ? notes : ""));
                }
                if (!any) {
                    System.out.println("No se encontraron préstamos para este usuario.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }
    }

    private Loan.ElementType dbToElementType(String db) {
        if (db == null) return null;
        switch (db) {
            case "DesktopPC": return Loan.ElementType.DESKTOP_PC;
            case "Laptop": return Loan.ElementType.LAPTOP;
            case "Tablet": return Loan.ElementType.TABLET;
            default: return null;
        }
    }

    private Loan.Status dbToStatus(String db) {
        if (db == null) return null;
        switch (db) {
            case "PENDING": return Loan.Status.PENDING;
            case "ACTIVE": return Loan.Status.ACTIVE;
            case "RETURNED": return Loan.Status.RETURNED;
            case "OVERDUE": return Loan.Status.OVERDUE;
            case "CANCELLED": return Loan.Status.CANCELLED;
            default: return null;
        }
    }
}
