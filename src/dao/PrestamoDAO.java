package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PrestamoDAO {
    
    // Registrar un nuevo préstamo
    public boolean registrarPrestamo(int copiaId, int personaId,
                                     Timestamp fechaPrestamo,
                                     Timestamp fechaVencimiento) {

        String sql = "INSERT INTO Prestamo " +
                     "(CopiaID, PersonaID, FechaPrestamo, FechaVencimiento, EstadoPrestamo) " +
                     "VALUES (?,?,?,?, 'Activo')";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, copiaId);
            ps.setInt(2, personaId);
            ps.setTimestamp(3, fechaPrestamo);
            ps.setTimestamp(4, fechaVencimiento);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando préstamo: " + e.getMessage());
            return false;
        }
    }

    // Registrar la devolución de un préstamo
    public boolean registrarDevolucion(int prestamoId, Timestamp fechaDevolucion) {
        String sql = "UPDATE Prestamo " +
                     "SET FechaDevolucion = ?, EstadoPrestamo = 'Devuelto' " +
                     "WHERE PrestamoID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, fechaDevolucion);
            ps.setInt(2, prestamoId);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando devolución: " + e.getMessage());
            return false;
        }
    }
}
