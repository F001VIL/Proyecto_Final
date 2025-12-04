package dao;

import modelo.Loan;

import java.sql.*;
import java.time.LocalDateTime;

public class LoanDAO {
    // Check if the user already has an active or pending loan.
    public boolean userHasActiveLoan(int userId) {
        String sql = "SELECT COUNT(1) FROM Loan WHERE UserID = ? AND Status IN ('PENDING','ACTIVE')";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) throw new SQLException("La conexión es nula");
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            // Wrap and keep message in Spanish for the user
            throw new RuntimeException("Error comprobando préstamos activos para el usuario " + userId + ": " + e.getMessage(), e);
        }
    }

    // Request a loan (student/teacher). Delegates to insertLoan which performs validations.
    public int requestLoan(Loan loan) {
        return insertLoan(loan);
    }

    // Insert a new loan into the DB. Returns generated LoanID.
    public int insertLoan(Loan loan) {
        if (loan == null || !loan.isValid()) {
            throw new IllegalArgumentException("El préstamo es nulo o inválido");
        }

        if (userHasActiveLoan(loan.getUserId())) {
            throw new IllegalStateException("Ya tiene un préstamo activo o pendiente");
        }

        String sql = "INSERT INTO Loan (UserID, ElementType, ElementID, Status, LoanDate, DueDate, ReturnDate, Notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (conn == null) throw new SQLException("La conexión es nula");

            ps.setInt(1, loan.getUserId());
            ps.setString(2, elementTypeToDb(loan.getElementType()));
            ps.setInt(3, loan.getElementId());
            ps.setString(4, statusToDb(loan.getStatus()));
            ps.setTimestamp(5, toTimestamp(loan.getLoanDate()));
            ps.setTimestamp(6, toTimestamp(loan.getDueDate()));
            ps.setTimestamp(7, toTimestamp(loan.getReturnDate()));
            ps.setString(8, loan.getNotes());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserción de préstamo fallida, ninguna fila afectada.");
            }

            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) {
                    return gen.getInt(1);
                } else {
                    throw new SQLException("Inserción fallida, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar préstamo: " + e.getMessage(), e);
        }
    }

    // Retrieve a loan by its ID.
    public Loan getLoanById(int loanId) {
        String sql = "SELECT LoanID, UserID, ElementType, ElementID, Status, LoanDate, DueDate, ReturnDate, Notes " +
                "FROM Loan WHERE LoanID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) throw new SQLException("La conexión es nula");
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan();
                    loan.setId(rs.getInt("LoanID"));
                    loan.setUserId(rs.getInt("UserID"));
                    loan.setElementType(dbToElementType(rs.getString("ElementType")));
                    loan.setElementId(rs.getInt("ElementID"));
                    loan.setStatus(dbToStatus(rs.getString("Status")));
                    Timestamp loanTs = rs.getTimestamp("LoanDate");
                    loan.setLoanDate(loanTs == null ? null : loanTs.toLocalDateTime());
                    Timestamp dueTs = rs.getTimestamp("DueDate");
                    loan.setDueDate(dueTs == null ? null : dueTs.toLocalDateTime());
                    Timestamp retTs = rs.getTimestamp("ReturnDate");
                    loan.setReturnDate(retTs == null ? null : retTs.toLocalDateTime());
                    loan.setNotes(rs.getString("Notes"));
                    return loan;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener préstamo con ID " + loanId + ": " + e.getMessage(), e);
        }
    }

    // Admin confirms a pending loan: sets Status = ACTIVE and updates LoanDate and DueDate.
    public void confirmLoan(int loanId, LocalDateTime dueDate) {
        String sql = "UPDATE Loan SET Status = ?, LoanDate = ?, DueDate = ? WHERE LoanID = ? AND Status = 'PENDING'";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) throw new SQLException("La conexión es nula");
            ps.setString(1, statusToDb(Loan.Status.ACTIVE));
            ps.setTimestamp(2, toTimestamp(LocalDateTime.now()));
            ps.setTimestamp(3, toTimestamp(dueDate));
            ps.setInt(4, loanId);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new IllegalStateException("No se pudo confirmar el préstamo. Verifique que exista y esté en estado PENDING.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al confirmar préstamo: " + e.getMessage(), e);
        }
    }

    // Admin registers the return: sets Status = RETURNED and updates ReturnDate.
    public void registerReturn(int loanId) {
        String sql = "UPDATE Loan SET Status = ?, ReturnDate = ? WHERE LoanID = ? AND Status = 'ACTIVE'";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) throw new SQLException("La conexión es nula");
            ps.setString(1, statusToDb(Loan.Status.RETURNED));
            ps.setTimestamp(2, toTimestamp(LocalDateTime.now()));
            ps.setInt(3, loanId);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new IllegalStateException("No se pudo registrar la devolución. Verifique que exista y esté en estado ACTIVE.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar devolución: " + e.getMessage(), e);
        }
    }

    // Convert Loan.ElementType to DB string.
    private String elementTypeToDb(Loan.ElementType et) {
        if (et == null) return null;
        switch (et) {
            case DESKTOP_PC: return "DesktopPC";
            case LAPTOP:     return "Laptop";
            case TABLET:     return "Tablet";
            default:         return et.name();
        }
    }

    // Parse DB string to Loan.ElementType.
    private Loan.ElementType dbToElementType(String db) {
        if (db == null) return null;
        switch (db) {
            case "DesktopPC": return Loan.ElementType.DESKTOP_PC;
            case "Laptop":    return Loan.ElementType.LAPTOP;
            case "Tablet":    return Loan.ElementType.TABLET;
            default:          return null;
        }
    }

    // Convert Loan.Status to DB string.
    private String statusToDb(Loan.Status s) {
        if (s == null) return null;
        switch (s) {
            case PENDING:   return "PENDING";
            case ACTIVE:    return "ACTIVE";
            case RETURNED:  return "RETURNED";
            case OVERDUE:   return "OVERDUE";
            case CANCELLED: return "CANCELLED";
            default:        return s.name();
        }
    }

    // Parse DB string to Loan.Status.
    private Loan.Status dbToStatus(String db) {
        if (db == null) return null;
        switch (db) {
            case "PENDING":   return Loan.Status.PENDING;
            case "ACTIVE":    return Loan.Status.ACTIVE;
            case "RETURNED":  return Loan.Status.RETURNED;
            case "OVERDUE":   return Loan.Status.OVERDUE;
            case "CANCELLED": return Loan.Status.CANCELLED;
            default:          return null;
        }
    }

    // Convert LocalDateTime to Timestamp (null-safe).
    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }

    /**
     * Returns available resources for the given element type by querying the corresponding table
     * and excluding rows that have a PENDING or ACTIVE loan of the same ElementType.
     */
    public java.util.List<AvailableResource> getAvailableResourcesByType(Loan.ElementType et) {
        if (et == null) return new java.util.ArrayList<>();

        String table;
        String idColumn = "ID";
        String labelColumn = "Code"; // most tables have a Code column; adjust if needed

        switch (et) {
            case DESKTOP_PC:
                table = "DesktopPC";
                break;
            case LAPTOP:
                table = "Laptop";
                break;
            case TABLET:
                table = "Tablet";
                break;
            default:
                return new java.util.ArrayList<>();
        }

        String sql = "SELECT t." + idColumn + " AS ElementID, COALESCE(t." + labelColumn + ", '') AS Label " +
                "FROM " + table + " t " +
                "WHERE t." + idColumn + " NOT IN (" +
                "  SELECT ElementID FROM Loan WHERE Status IN ('PENDING','ACTIVE') AND ElementType = ?" +
                ") ORDER BY t." + idColumn;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) throw new SQLException("La conexión es nula");
            ps.setString(1, elementTypeToDb(et));

            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<AvailableResource> list = new java.util.ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("ElementID");
                    String label = rs.getString("Label");
                    list.add(new AvailableResource(id, label, et));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar recursos disponibles para " + et + ": " + e.getMessage(), e);
        }
    }

    /**
     * Aggregate available resources from all element types.
     */
    public java.util.List<AvailableResource> getAllAvailableResources() {
        java.util.List<AvailableResource> all = new java.util.ArrayList<>();
        for (Loan.ElementType et : Loan.ElementType.values()) {
            all.addAll(getAvailableResourcesByType(et));
        }
        return all;
    }

    /* Simple DTO to hold available resource info. */
    public static class AvailableResource {
        private final int elementId;
        private final String label;
        private final Loan.ElementType elementType;

        public AvailableResource(int elementId, String label, Loan.ElementType elementType) {
            this.elementId = elementId;
            this.label = label;
            this.elementType = elementType;
        }

        public int getElementId() {
            return elementId;
        }

        public String getLabel() {
            return label;
        }

        public Loan.ElementType getElementType() {
            return elementType;
        }

        @Override
        public String toString() {
            return "AvailableResource{" +
                    "elementId=" + elementId +
                    ", label='" + label + '\'' +
                    ", elementType=" + elementType +
                    '}';
        }
    }
}
