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

public class LoanLibrarianManager {
    private final LoanDAO loanDAO = new LoanDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void runMenu() {
        while (true) {
            System.out.println();
            System.out.println("===== Menú de préstamos (Bibliotecario) =====");
            System.out.println("1) Listar solicitudes pendientes");
            System.out.println("2) Confirmar (activar) solicitud");
            System.out.println("3) Registrar devolución");
            System.out.println("4) Listar todos los préstamos");
            System.out.println("0) Salir");
            System.out.print("Seleccione opción: ");

            String opt = scanner.nextLine().trim();
            if (opt.isEmpty()) continue;

            switch (opt) {
                case "1":
                    listLoansByStatus("PENDING");
                    break;
                case "2":
                    confirmLoan();
                    break;
                case "3":
                    registerReturn();
                    break;
                case "4":
                    listLoansByStatus(null);
                    break;
                case "0":
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void confirmLoan() {
        System.out.println("\n-- Confirmar solicitud --");
        System.out.print("Ingrese ID del préstamo a confirmar: ");
        String idStr = scanner.nextLine().trim();
        int loanId;
        try {
            loanId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Loan loan;
        try {
            loan = loanDAO.getLoanById(loanId);
        } catch (RuntimeException e) {
            System.out.println("Error al obtener préstamo: " + e.getMessage());
            return;
        }
        if (loan == null) {
            System.out.println("No existe préstamo con ID " + loanId);
            return;
        }
        if (loan.getStatus() != Loan.Status.PENDING) {
            System.out.println("Sólo se pueden confirmar préstamos en estado PENDING. Estado actual: " + loan.getStatus());
            return;
        }

        System.out.println("Préstamo seleccionado:");
        printLoanSummary(loan);

        System.out.print("Duración en días para la fecha de vencimiento (ej. 14): ");
        String daysStr = scanner.nextLine().trim();
        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Número de días inválido.");
            return;
        }

        LocalDateTime dueDate = LocalDateTime.now().plusDays(days);
        try {
            loanDAO.confirmLoan(loanId, dueDate);
            System.out.println("Préstamo confirmado. Fecha de vencimiento = " + dueDate);
        } catch (IllegalStateException ise) {
            System.out.println("No se pudo confirmar: " + ise.getMessage());
        } catch (RuntimeException re) {
            System.out.println("Error al confirmar préstamo: " + re.getMessage());
        }
    }

    private void registerReturn() {
        System.out.println("\n-- Registrar devolución --");
        System.out.print("Ingrese ID del préstamo que se devuelve: ");
        String idStr = scanner.nextLine().trim();
        int loanId;
        try {
            loanId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Loan loan;
        try {
            loan = loanDAO.getLoanById(loanId);
        } catch (RuntimeException e) {
            System.out.println("Error al obtener préstamo: " + e.getMessage());
            return;
        }
        if (loan == null) {
            System.out.println("No existe préstamo con ID " + loanId);
            return;
        }
        if (loan.getStatus() != Loan.Status.ACTIVE) {
            System.out.println("Sólo se pueden registrar devoluciones de préstamos en estado ACTIVE. Estado actual: " + loan.getStatus());
            return;
        }

        System.out.println("Préstamo seleccionado:");
        printLoanSummary(loan);

        System.out.print("Confirmar registro de devolución? (s/N): ");
        String conf = scanner.nextLine().trim().toLowerCase();
        if (!conf.equals("s") && !conf.equals("y")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            loanDAO.registerReturn(loanId);
            System.out.println("Devolución registrada.");
        } catch (IllegalStateException ise) {
            System.out.println("No se pudo registrar devolución: " + ise.getMessage());
        } catch (RuntimeException re) {
            System.out.println("Error al registrar devolución: " + re.getMessage());
        }
    }

    /**
     * Lists loans. If statusFilter is null lists all loans; otherwise filters by Status value.
     */
    private void listLoansByStatus(String statusFilter) {
        System.out.println();
        String header = statusFilter == null ? "-- Todos los préstamos --" : "-- Préstamos en estado: " + statusFilter + " --";
        System.out.println(header);

        String sql = "SELECT LoanID, UserID, ElementType, ElementID, Status, LoanDate, DueDate, ReturnDate, Notes " +
                "FROM Loan ";
        if (statusFilter != null) {
            sql += "WHERE Status = ? ";
        }
        sql += "ORDER BY LoanDate DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Error: conexión nula.");
                return;
            }

            if (statusFilter != null) {
                ps.setString(1, statusFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    int id = rs.getInt("LoanID");
                    int userId = rs.getInt("UserID");
                    String etStr = rs.getString("ElementType");
                    int elementId = rs.getInt("ElementID");
                    String statusStr = rs.getString("Status");
                    Timestamp loanTs = rs.getTimestamp("LoanDate");
                    Timestamp dueTs = rs.getTimestamp("DueDate");
                    Timestamp retTs = rs.getTimestamp("ReturnDate");
                    String notes = rs.getString("Notes");

                    System.out.println("----------------------------------------");
                    System.out.println("ID: " + id + "  |  UsuarioID: " + userId);
                    System.out.println("Recurso: " + (etStr != null ? etStr : "N/A") + " (ID " + elementId + ")");
                    System.out.println("Estado: " + (statusStr != null ? statusStr : "N/A"));
                    System.out.println("Fecha préstamo: " + (loanTs != null ? loanTs.toLocalDateTime() : "N/A"));
                    System.out.println("Fecha vencimiento: " + (dueTs != null ? dueTs.toLocalDateTime() : "N/A"));
                    System.out.println("Fecha devolución: " + (retTs != null ? retTs.toLocalDateTime() : "N/A"));
                    System.out.println("Notas: " + (notes != null ? notes : ""));
                }
                if (!any) {
                    System.out.println("No se encontraron préstamos para la consulta.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }
    }

    private void printLoanSummary(Loan loan) {
        if (loan == null) return;
        System.out.println("ID: " + loan.getId());
        System.out.println("UsuarioID: " + loan.getUserId());
        System.out.println("Recurso: " + (loan.getElementType() != null ? loan.getElementType() : "N/A") + " (ID " + loan.getElementId() + ")");
        System.out.println("Estado: " + (loan.getStatus() != null ? loan.getStatus() : "N/A"));
        System.out.println("Fecha préstamo: " + (loan.getLoanDate() != null ? loan.getLoanDate() : "N/A"));
        System.out.println("Fecha vencimiento: " + (loan.getDueDate() != null ? loan.getDueDate() : "N/A"));
        System.out.println("Fecha devolución: " + (loan.getReturnDate() != null ? loan.getReturnDate() : "N/A"));
        System.out.println("Notas: " + (loan.getNotes() != null ? loan.getNotes() : ""));
    }
}
